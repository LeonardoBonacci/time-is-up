CREATE STREAM unmoved
 (id VARCHAR,
  lat DOUBLE,
  lon DOUBLE)
 WITH (KAFKA_TOPIC = 'unmoved',
       VALUE_FORMAT = 'avro',
       VALUE_AVRO_SCHEMA_FULL_NAME='guru.bonacci.timesup.model.Unmoved',
       KEY = 'id',
       PARTITIONS = 1);

CREATE STREAM unmoved_geo
  WITH (KAFKA_TOPIC = 'unmoved_geo',
        VALUE_FORMAT = 'avro',
        VALUE_AVRO_SCHEMA_FULL_NAME='guru.bonacci.timesup.model.UnmovedGeo',
        PARTITIONS = 1)
  AS SELECT id, lat, lon, GEOHASH(lat, lon, 8) AS geohash
  FROM unmoved;

INSERT INTO unmoved (id, lat, lon) VALUES ('Torpedo7Albany', 1.0, 1.0);
INSERT INTO unmoved (id, lat, lon) VALUES ('TEST', 10.0, -10.0);

-- 'CREATE TABLE unmoved_geo_t' has become part of the track-enricher kstream-app

CREATE STREAM track_geo
  (tracking_number VARCHAR,
   mover_id VARCHAR,
   unmoved_id VARCHAR,
   unmoved_geohash VARCHAR,
   unmoved_lat DOUBLE,
   unmoved_lon DOUBLE)
WITH (KAFKA_TOPIC = 'track_geo',
     VALUE_FORMAT = 'avro',
     VALUE_AVRO_SCHEMA_FULL_NAME='guru.bonacci.timesup.model.TrackGeo',
     KEY = 'mover_id');

CREATE STREAM mover
  (id VARCHAR KEY,
   lat DOUBLE,
   lon DOUBLE)
  WITH (KAFKA_TOPIC='mover',
        VALUE_FORMAT='json',
        PARTITIONS = 1);

INSERT INTO mover (id, lat, lon) VALUES ('moverid', 0.90, 0.90);

-- GEOHASH MEANING
--4 -> 39.1km x 19.5km
--5 -> 4.9km x 4.9km
--6 -> 1.2km x 609.4m
--7 -> 152.9m x 152.4m
--8 -> 38.2m x 19m

-- to facilitate filtering we join stream-stream and allow 24 hours of tracing
-- after the track has been registred
CREATE STREAM trace_unfiltered
  WITH (KAFKA_TOPIC = 'trace_unfiltered',
        VALUE_FORMAT = 'avro',
        VALUE_AVRO_SCHEMA_FULL_NAME='guru.bonacci.timesup.model.Trace'
        PARTITIONS = 1)
  AS SELECT
          mover.id AS mover_id,
          mover.lat AS mover_lat,
          mover.lon AS mover_lon,
          CASE
              WHEN GEO_DISTANCE(mover.lat, mover.lon, track.unmoved_lat, track.unmoved_lon) < 1 THEN GEOHASH(lat, lon, 8)
              WHEN GEO_DISTANCE(mover.lat, mover.lon, track.unmoved_lat, track.unmoved_lon) < 10 THEN GEOHASH(lat, lon, 7)
              WHEN GEO_DISTANCE(mover.lat, mover.lon, track.unmoved_lat, track.unmoved_lon) < 50 THEN GEOHASH(lat, lon, 6)
              WHEN GEO_DISTANCE(mover.lat, mover.lon, track.unmoved_lat, track.unmoved_lon) < 100 THEN GEOHASH(lat, lon, 5)
              ELSE GEOHASH(lat, lon, 4)
          END AS mover_geohash,
          track.tracking_number AS tracking_number,
          track.unmoved_id AS unmoved_id,
          track.unmoved_lat AS unmoved_lat,
          track.unmoved_lon AS unmoved_lon,
          track.unmoved_geohash AS unmoved_geohash
  FROM mover
  INNER JOIN track_geo AS track WITHIN (1 DAY, 0 SECONDS) ON mover.id = track.mover_id
  PARTITION BY track.tracking_number;

-- movers on deleted/unregistred tracks are filtered from tracing
-- table with 'tracks in progress'
CREATE TABLE track_t
    (rowkey VARCHAR KEY,
     tracking_number VARCHAR,
     mover_id VARCHAR,
     unmoved_id VARCHAR)
  WITH (KAFKA_TOPIC = 'track',
       VALUE_FORMAT = 'avro',
       VALUE_AVRO_SCHEMA_FULL_NAME='guru.bonacci.timesup.model.Track'
       KEY = 'tracking_number',
       PARTITIONS = 1);

-- include 'trace in progress' and exclude 'trace no longer in progress'
CREATE STREAM trace
  WITH (KAFKA_TOPIC = 'trace',
        VALUE_FORMAT = 'avro',
        VALUE_AVRO_SCHEMA_FULL_NAME='guru.bonacci.timesup.model.Trace',
        PARTITIONS = 1)
  AS SELECT
          trace.mover_id AS mover_id,
          trace.mover_lat AS mover_lat,
          trace.mover_lon as mover_lon,
          trace.mover_geohash AS mover_geohash,
          trace.tracking_number AS tracking_number,
          trace.unmoved_id AS unmoved_id,
          trace.unmoved_lat AS unmoved_lat,
          trace.unmoved_lon AS unmoved_lon,
          trace.unmoved_geohash AS unmoved_geohash
  FROM trace_unfiltered AS trace
  INNER JOIN track_t AS track ON trace.tracking_number = track.tracking_number
  PARTITION BY trace.mover_id;

/**
./kafka-avro-console-consumer \
    --bootstrap-server localhost:9092 \
    --property schema.registry.url=http://localhost:8081 \
    --topic trace \
    --from-beginning
*/

INSERT INTO mover (id, lat, lon) VALUES ('moverid', 0.71, 0.96);
INSERT INTO mover (id, lat, lon) VALUES ('moverid', 1.0, 1.0);

CREATE SINK CONNECTOR tile WITH (
    'tasks.max' = '1',
    'connector.class' = 'guru.bonacci.kafka.connect.tile38.Tile38SinkConnector',
    'topics' = 'unmoved,trace',
    'key.converter' = 'org.apache.kafka.connect.storage.StringConverter',
    'value.converter' = 'io.confluent.connect.avro.AvroConverter',
    'value.converter.schema.registry.url' = 'http://schema-registry:8081',
    'tile38.topic.unmoved' = 'SET unmoved event.ID POINT event.LAT event.LON',
    'tile38.topic.trace' = 'SET trace event.MOVER_ID POINT event.MOVER_LAT event.MOVER_LON',
    'tile38.host' = 'tile38',
    'tile38.port' = 9851,
    'errors.tolerance' = 'all',
    'errors.log.enable' = true,
    'errors.log.include.messages' = true);

-- topic arrival_raw: message.timestamp.type.LogAppendTime
-- docker run --net=host -it tile38/tile38 tile38-cli
-- SETHOOK arrivals kafka://broker:29092/arrival_raw NEARBY trace FENCE NODWELL ROAM unmoved * 100


CREATE STREAM arrival_raw (id STRING, nearby STRUCT<id STRING>)
  WITH (KAFKA_TOPIC = 'arrival_raw',
        VALUE_FORMAT='json');

-- 'nearby is not null' filters out the faraway messages
CREATE STREAM arrival
  WITH (KAFKA_TOPIC = 'arrival',
        VALUE_FORMAT = 'avro',
        VALUE_AVRO_SCHEMA_FULL_NAME='guru.bonacci.timesup.model.Arrival',
        PARTITIONS = 1)
  AS SELECT id as mover_id,
          nearby->id as unmoved_id
  FROM arrival_raw
  WHERE nearby IS NOT NULL
  PARTITION BY id;

-- this stream can be used for multiple estimation algorithms
CREATE STREAM pickup
  WITH (KAFKA_TOPIC = 'pickup',
        VALUE_FORMAT = 'avro',
        VALUE_AVRO_SCHEMA_FULL_NAME='guru.bonacci.timesup.model.Pickup',
        PARTITIONS = 1)
    AS SELECT
      trace.mover_id AS mover_id,
      trace.mover_lat AS mover_lat,
      trace.mover_lon as mover_lon,
      trace.mover_geohash AS mover_geohash,
      trace.tracking_number AS tracking_number,
      trace.unmoved_id AS unmoved_id,
      trace.unmoved_lat AS unmoved_lat,
      trace.unmoved_lon AS unmoved_lon,
      trace.unmoved_geohash AS unmoved_geohash,
      arrival.rowtime - trace.rowtime as togo_ms
    FROM trace AS trace
    INNER JOIN arrival as arrival WITHIN (0 MILLISECONDS, 1 HOUR) ON arrival.mover_id = trace.mover_id
    WHERE arrival.unmoved_id = trace.unmoved_id;

CREATE STREAM geohash_estimate
  WITH (VALUE_FORMAT = 'avro',
      KAFKA_TOPIC = 'geohash_estimate',
      VALUE_AVRO_SCHEMA_FULL_NAME='guru.bonacci.timesup.model.GeohashEstimate',
      PARTITIONS = 1)
  AS SELECT
    pickup.togo_ms as togo_ms,
    mover_geohash + '/' + unmoved_geohash as hashkey
  FROM pickup
  PARTITION BY (mover_geohash + '/' + unmoved_geohash);

CREATE TABLE geohash_avg_estimate_t
  WITH (KAFKA_TOPIC = 'geohash_avg_estimate',
        VALUE_FORMAT = 'avro',
        VALUE_AVRO_SCHEMA_FULL_NAME='guru.bonacci.timesup.model.GeohashAvgEstimate',
        PARTITIONS = 1)
    AS SELECT hashkey,
      AVG(togo_ms) as togo_ms
      FROM pickup_time
      GROUP BY hashkey
      EMIT CHANGES;

CREATE STREAM trace_to_geohash_avg_estimate
  WITH (VALUE_FORMAT = 'avro',
      VALUE_AVRO_SCHEMA_FULL_NAME='guru.bonacci.timesup.model.Something'
      KAFKA_TOPIC = 'trace_to_geohash_avg_estimate',
      PARTITIONS = 1)
  AS SELECT *,
    mover_geohash + '/' + unmoved_geohash as hashkey
  FROM trace
  PARTITION BY (mover_geohash + '/' + unmoved_geohash);

CREATE STREAM homeward
    WITH (VALUE_FORMAT = 'avro',
          VALUE_AVRO_SCHEMA_FULL_NAME='guru.bonacci.timesup.model.Homeward'
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
