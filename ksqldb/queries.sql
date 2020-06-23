CREATE STREAM unmoved
 (id VARCHAR,
  latitude DOUBLE,
  longitude DOUBLE)
 WITH (KAFKA_TOPIC = 'unmoved',
       VALUE_FORMAT = 'protobuf',
       KEY = 'id',
       PARTITIONS = 1);

CREATE STREAM unmoved_geo
  WITH (KAFKA_TOPIC = 'unmoved_geo',
        VALUE_FORMAT = 'protobuf',
        PARTITIONS = 1)
  AS SELECT id, latitude, longitude, GEOHASH(latitude, longitude, 8) AS geohash
  FROM unmoved;

INSERT INTO unmoved (id, latitude, longitude) VALUES ('Torpedo7Albany', 1.0, 1.0);

-- CREATE TABLE unmoved_geo_t
-- this has become part of a the track-enricher kstream app

CREATE STREAM track
  (rowkey VARCHAR KEY,
   tracking_number VARCHAR,
   mover_id VARCHAR,
   unmoved_id VARCHAR)
WITH (KAFKA_TOPIC = 'track',
     VALUE_FORMAT = 'protobuf',
     KEY = 'tracking_number',
     PARTITIONS = 1);

INSERT INTO track (rowkey, tracking_number, mover_id, unmoved_id) VALUES ('3SABC1234567890', '3SABC1234567890', 'thisisme', 'Torpedo7Albany');

-- CREATE STREAM track_geo
-- this has become part of a KStream app

CREATE STREAM track_geo
  (tracking_number VARCHAR,
   mover_id VARCHAR,
   unmoved_id VARCHAR)
WITH (KAFKA_TOPIC = 'track_geo',
     VALUE_FORMAT = 'protobuf',
     KEY = 'mover_id');

CREATE STREAM mover
  (id VARCHAR,
   lat DOUBLE,
   lon DOUBLE)
  WITH (KAFKA_TOPIC='mover',
        VALUE_FORMAT='protobuf',
        KEY = 'id',
        PARTITIONS = 2);

INSERT INTO mover (id, lat, lon) VALUES ('thisisme', 0.61, 0.94);
INSERT INTO mover (id, lat, lon) VALUES ('thisisme', 0.71, 0.96);
INSERT INTO mover (id, lat, lon) VALUES ('thisisme', 0.81, 0.98);
INSERT INTO mover (id, lat, lon) VALUES ('thisisme', 0.91, 0.99);
INSERT INTO mover (id, lat, lon) VALUES ('thisisme', 1.0, 1.0);

-- mover + track = trace
CREATE STREAM trace
  WITH (VALUE_FORMAT = 'protobuf',
        KAFKA_TOPIC = 'trace',
        PARTITIONS = 2)
  AS SELECT
          mover.id AS mover_id,
          mover.lat AS lat,
          mover.lon AS lon,
          GEOHASH(mover.lat, mover.lon, 6) AS mover_geohash, -- add variable length
          track.tracking_number AS tracking_number,
          track.unmoved_id AS unmoved_id,
          track.unmoved_geohash AS unmoved_geohash
  FROM mover
  INNER JOIN track_geo AS track WITHIN (1 DAY, 0 SECONDS) ON mover.id = track.mover_id
  PARTITION BY mover.id;


CREATE SINK CONNECTOR tile WITH (
  'tasks.max' = '1',
  'connector.class' = 'guru.bonacci.kafka.connect.tile38.Tile38SinkConnector',
  'topics' = 'unmoved,trace',
  'key.converter' = 'org.apache.kafka.connect.storage.StringConverter',
  'value.converter' = 'io.confluent.connect.protobuf.ProtobufConverter',
  'value.converter.schema.registry.url' = 'http://schema-registry:8081',
  'tile38.topic.unmoved' = 'SET unmoved event.ID POINT event.LATITUDE event.LONGITUDE',
  'tile38.topic.trace' = 'SET trace event.MOVER_ID POINT event.LAT event.LON',
  'tile38.host' = 'tile38',
  'tile38.port' = 9851,
  'errors.tolerance' = 'all',
  'errors.log.enable' = true,
  'errors.log.include.messages' = true);

-- topic arrival_raw: message.timestamp.type.LogAppendTime
-- docker run --net=host -it tile38/tile38 tile38-cli
-- SETHOOK arrivals kafka://broker:9092/arrival_raw NEARBY trace FENCE NODWELL ROAM unmoved * 100

CREATE STREAM arrival_raw (id STRING, nearby STRUCT<id STRING>)
  WITH (KAFKA_TOPIC = 'arrival_raw',
        VALUE_FORMAT='json');

-- nearby is not null filters out the faraway messages
CREATE STREAM arrival
  WITH (VALUE_FORMAT = 'protobuf',
        KAFKA_TOPIC = 'arrival',
        PARTITIONS = 2)
  AS SELECT id as mover_id,
          nearby->id as unmoved_id
  FROM arrival_raw
  WHERE nearby IS NOT NULL
  PARTITION BY id;

CREATE STREAM pickup
  WITH (VALUE_FORMAT = 'protobuf',
      KAFKA_TOPIC = 'pickup',
      PARTITIONS = 1)
  AS SELECT
    trace.mover_id,
    trace.mover_geohash,
    trace.unmoved_geohash,
    trace.unmoved_id,
    arrival.rowtime - trace.rowtime as togo_ms,
    mover_geohash + '/' + unmoved_geohash as hashkey
  FROM trace AS trace
  INNER JOIN arrival as arrival WITHIN (0 MILLISECONDS, 1 HOUR) ON arrival.mover_id = trace.mover_id
  WHERE arrival.unmoved_id = trace.unmoved_id
  PARTITION BY (mover_geohash + '/' + unmoved_geohash);

-- 2 partitions!
CREATE TABLE estimate_t
    WITH (VALUE_FORMAT = 'protobuf',
        KAFKA_TOPIC = 'estimate',
        PARTITIONS = 1)
    AS SELECT hashkey,
      AVG(togo_ms) as togo_ms
      FROM pickup
      GROUP BY hashkey
      EMIT CHANGES;

CREATE STREAM trace_to_estimate
  WITH (VALUE_FORMAT = 'protobuf',
      KAFKA_TOPIC = 'trace_to_estimate',
      PARTITIONS = 1)
  AS SELECT *,
    mover_geohash + '/' + unmoved_geohash as hashkey
  FROM trace
  PARTITION BY (mover_geohash + '/' + unmoved_geohash);

CREATE STREAM homeward
    WITH (VALUE_FORMAT = 'protobuf',
          KAFKA_TOPIC = 'homeward',
          PARTITIONS = 1)
    AS SELECT
     trace.unmoved_id as unmoved_id,
     trace.lat as lat,
     trace.lon as lon,
     trace.tracking_number AS tracking_number,
     estimate.togo_ms as togo_ms
    FROM trace_to_estimate AS trace
    INNER JOIN estimate_t as estimate ON estimate.hashkey = trace.hashkey
    PARTITION BY trace.tracking_number;
