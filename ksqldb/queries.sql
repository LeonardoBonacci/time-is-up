-- first generate some data with sample producer

CREATE STREAM mover
  (id VARCHAR,
   lat DOUBLE,
   lon DOUBLE)
  WITH (KAFKA_TOPIC='mover',
        VALUE_FORMAT='protobuf',
        KEY = 'id');

-- add insert statements

CREATE STREAM unmoved
 (id VARCHAR,
  latitude DOUBLE,
  longitude DOUBLE)
 WITH (KAFKA_TOPIC = 'unmoved',
       VALUE_FORMAT = 'protobuf',
       KEY = 'id',
       PARTITIONS = 1);

INSERT INTO unmoved (id, latitude, longitude) VALUES ('Torpedo7Albany', 1.0, 1.0);

CREATE STREAM unmoved_geo
   WITH (VALUE_FORMAT = 'protobuf',
         KAFKA_TOPIC = 'unmoved_geo',
         PARTITIONS = 1)
   AS SELECT id, latitude, longitude, GEOHASH(latitude, longitude, 6) AS geohash
   FROM unmoved;

CREATE TABLE unmoved_geo_t
 (rowkey VARCHAR KEY,
  id VARCHAR,
  latitude DOUBLE,
  longitude DOUBLE,
  geohash DOUBLE)
WITH (KAFKA_TOPIC = 'unmoved_geo',
    VALUE_FORMAT = 'protobuf',
    KEY = 'id',
    PARTITIONS = 1);

CREATE TABLE track_t
  (rowkey VARCHAR KEY,
   tracking_number VARCHAR,
   mover_id VARCHAR,
   unmoved_id VARCHAR)
WITH (KAFKA_TOPIC = 'track',
     VALUE_FORMAT = 'protobuf',
     KEY = 'mover_id',
     PARTITIONS = 1);

INSERT INTO track_t (rowkey, tracking_number, mover_id, unmoved_id) VALUES ('me', '3SABC1234567890', 'me', 'Torpedo7Albany');

CREATE STREAM trace
  WITH (VALUE_FORMAT = 'protobuf',
        KAFKA_TOPIC = 'trace',
        PARTITIONS = 1)
  AS SELECT mover.id AS mover_id,
          mover.lat AS lat,
          mover.lon AS lon,
          track.tracking_number AS tracking_number,
          track.unmoved_id AS unmoved_id
  FROM mover
  INNER JOIN track_t AS track ON mover.id = track.mover_id
  PARTITION BY mover.id;

CREATE STREAM trace_by_unmoved_id
    WITH (VALUE_FORMAT = 'protobuf',
          KAFKA_TOPIC = 'trace_by_unmoved_id',
          PARTITIONS = 1)
    AS SELECT *
    FROM trace
    PARTITION BY unmoved_id;

CREATE STREAM trace_geo
    WITH (VALUE_FORMAT = 'protobuf',
          KAFKA_TOPIC = 'trace_to_dest',
          PARTITIONS = 1)
    AS SELECT
        trace.mover_id,
        trace.lat AS mover_lat,
        trace.lon AS mover_lon,
        ABS(trace.lat) AS mover_geohash,
        unmoved.latitude AS unmoved_lat,
        unmoved.longitude AS unmoved_lon,
        unmoved.geohash AS unmoved_geohash,
        trace.tracking_number AS tracking_number,
        trace.unmoved_id AS unmoved_id
    FROM trace_by_unmoved_id AS trace
    INNER JOIN unmoved_geo_t AS unmoved ON unmoved.id = trace.unmoved_id
    PARTITION BY trace.unmoved_id;

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

-- topic tile_arrival: message.timestamp.type.LogAppendTime
-- docker run --net=host -it tile38/tile38 tile38-cli
-- SETHOOK arrivals kafka://broker:9092/tile_arrival NEARBY trace FENCE NODWELL ROAM unmoved * 100

CREATE STREAM tile_arrival (id STRING, nearby STRUCT<id STRING>)
  WITH (KAFKA_TOPIC = 'tile_arrival',
        VALUE_FORMAT='json');

-- nearby is not null filters out the faraway messages
CREATE STREAM arrival
  WITH (VALUE_FORMAT = 'protobuf',
        KAFKA_TOPIC = 'arrival',
        PARTITIONS = 1)
  AS SELECT id as mover_id,
          nearby->id as unmoved_id
  FROM tile_arrival
  WHERE nearby IS NOT NULL
  PARTITION BY id;

-- back to the future
CREATE STREAM pickup
  WITH (VALUE_FORMAT = 'protobuf',
      KAFKA_TOPIC = 'pickup',
      PARTITIONS = 1)
  AS SELECT
    trace.mover_id,
    trace.mover_lat,
    trace.mover_lon,
    trace.mover_geohash,
    trace.unmoved_lat,
    trace.unmoved_lon,
    trace.unmoved_geohash,
    trace.tracking_number,
    trace.unmoved_id,
    arrival.rowtime - trace.rowtime as togo_ms
  FROM trace_to_geo AS trace
  INNER JOIN arrival WITHIN (0 MILLISECONDS, 1 HOUR) ON arrival.mover_id = trace.mover_id
  WHERE arrival.unmoved_id = trace.unmoved_id
  PARTITION BY arrival.unmoved_id;

CREATE STREAM ready
  WITH (VALUE_FORMAT = 'protobuf',
      KAFKA_TOPIC = 'ready',
      PARTITIONS = 1)
  AS SELECT
    CAST(mover_geohash AS STRING) + '/' + CAST(mover_geohash AS STRING) as key,
    togo_ms
  FROM pickup
  PARTITION BY CAST(mover_geohash AS STRING) + '/' + CAST(mover_geohash AS STRING);

CREATE TABLE estimate
    WITH (VALUE_FORMAT = 'protobuf',
        KAFKA_TOPIC = 'estimate',
        PARTITIONS = 1)
    AS SELECT key,
      AVG(togo)
      FROM ready
      GROUP BY key
      EMIT CHANGES;

CREATE STREAM trace_ready
  WITH (VALUE_FORMAT = 'protobuf',
      KAFKA_TOPIC = 'trace_ready',
      PARTITIONS = 1)
  AS SELECT *,
    CAST(mover_geohash AS STRING) + '/' + CAST(mover_geohash AS STRING) as key
  FROM pickup
  PARTITION BY CAST(mover_geohash AS STRING) + '/' + CAST(mover_geohash AS STRING);

CREATE STREAM home
    WITH (VALUE_FORMAT = 'protobuf',
          KAFKA_TOPIC = 'home',
          PARTITIONS = 1)
    AS SELECT *
    FROM trace_ready AS trace
    INNER JOIN estimate ON estimate.key = trace.key
    PARTITION BY trace.TRACE_UNMOVED_ID;
