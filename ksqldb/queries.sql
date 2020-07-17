CREATE STREAM unmoved
 (rowkey VARCHAR KEY,
  id VARCHAR, --id in payload is used by connector
  lat DOUBLE,
  lon DOUBLE)
 WITH (KAFKA_TOPIC = 'unmoved', --compacted topic
       VALUE_FORMAT = 'json',
       PARTITIONS = 2);

CREATE TABLE unmoved_t
    (id VARCHAR PRIMARY KEY,
     lat DOUBLE,
     lon DOUBLE)
  WITH (KAFKA_TOPIC = 'unmoved',
       VALUE_FORMAT = 'json');

CREATE STREAM track
 (tracking_number VARCHAR KEY,
  mover_id VARCHAR,
  unmoved_id VARCHAR
  )
 WITH (KAFKA_TOPIC = 'track', --compacted topic & retention period 86400000 ms
       VALUE_FORMAT = 'json',
       PARTITIONS = 12);

CREATE STREAM track_repart
  WITH (KAFKA_TOPIC = 'track_repart', --retention period 86400000 ms
        VALUE_FORMAT = 'json',
        PARTITIONS = 2)
  AS SELECT * FROM track;

CREATE STREAM track_geo
  WITH (KAFKA_TOPIC = 'track_geo', --retention period 86400000 ms
        VALUE_FORMAT = 'json',
        PARTITIONS = 12)
  AS SELECT
    track.mover_id,
    track.tracking_number,
    track.unmoved_id,
    GEOHASH(lat, lon, 8) AS unmoved_geohash,
    lat AS unmoved_lat,
    lon AS unmoved_lon
  FROM track_repart AS track
  INNER JOIN unmoved_t AS unmoved ON track.unmoved_id = unmoved.id
  PARTITION BY track.mover_id;

  -- table with 'tracks in progress'
  -- movers on deleted/unregistred tracks are filtered from tracing
CREATE TABLE track_t
  (tracking_number VARCHAR PRIMARY KEY,
   mover_id VARCHAR,
   unmoved_id VARCHAR)
WITH (KAFKA_TOPIC = 'track',
     VALUE_FORMAT = 'json');

CREATE STREAM mover
  (id VARCHAR KEY,
   lat DOUBLE,
   lon DOUBLE)
  WITH (KAFKA_TOPIC='mover', --retention period 86400000 ms
        VALUE_FORMAT='json',
        PARTITIONS = 12);

-- GEOHASH MEANING
--4 -> 39.1km x 19.5km
--5 -> 4.9km x 4.9km
--6 -> 1.2km x 609.4m
--7 -> 152.9m x 152.4m
--8 -> 38.2m x 19m

-- to facilitate filtering we join stream-stream and allow 24 hours of tracing
-- after the track has been registred
CREATE STREAM trace_unfiltered
  WITH (KAFKA_TOPIC = 'trace_unfiltered', --retention period 86400000 ms
        VALUE_FORMAT = 'json',
        PARTITIONS = 12)
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
  INNER JOIN track_geo AS track WITHIN (1 DAY, 0 SECONDS) ON mover.id = track.mover_id;


-- sample data
INSERT INTO unmoved (rowkey, id, lat, lon) VALUES ('Torpedo7Albany', 'Torpedo7Albany', 1.0, 1.0);
INSERT INTO unmoved (rowkey, id, lat, lon) VALUES ('Torpedo7Albany', 'TEST', 10.0, -10.0);
INSERT INTO track (tracking_number, mover_id, unmoved_id) VALUES ('somenumber', 'thisisme', 'Torpedo7Albany');
INSERT INTO mover (id, lat, lon) VALUES ('thisisme', 0.90, 0.90);
INSERT INTO mover (id, lat, lon) VALUES ('thisisme', 0.71, 0.96);
INSERT INTO mover (id, lat, lon) VALUES ('thisisme', 1.0, 1.0);


-- include 'trace in progress' and exclude 'trace no longer in progress'
CREATE STREAM trace
  WITH (KAFKA_TOPIC = 'trace', --retention period 86400000 ms
        VALUE_FORMAT = 'json',
        PARTITIONS = 12)
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
  INNER JOIN track_t AS track ON trace.tracking_number = track.tracking_number;

CREATE SINK CONNECTOR tile WITH (
   'tasks.max' = '1',
   'connector.class' = 'guru.bonacci.kafka.connect.tile38.Tile38SinkConnector',
   'topics' = 'unmoved,trace',
   'key.converter' = 'org.apache.kafka.connect.storage.StringConverter',
   'value.converter' = 'org.apache.kafka.connect.json.JsonConverter',
   'value.converter.schemas.enable' = false,
   'tile38.topic.unmoved' = 'SET unmoved event.ID POINT event.LAT event.LON',
   'tile38.topic.trace' = 'SET trace event.MOVER_ID POINT event.MOVER_LAT event.MOVER_LON',
   'tile38.host' = 'tile38',
   'tile38.port' = 9851);

-- topic arrival_raw: message.timestamp.type.LogAppendTime 2 partitions
-- docker run --net=host -it tile38/tile38 tile38-cli
-- SETHOOK arrivals kafka://broker:29092/arrival_raw NEARBY trace FENCE NODWELL ROAM unmoved * 100

CREATE STREAM arrival_raw (id STRING, nearby STRUCT<id STRING>)
  WITH (KAFKA_TOPIC = 'arrival_raw', --retention period 86400000 ms
        VALUE_FORMAT='json');

-- 'nearby is not null' filters out the faraway messages
CREATE STREAM arrival
  WITH (KAFKA_TOPIC = 'arrival', --retention period 86400000 ms
        VALUE_FORMAT = 'json',
        PARTITIONS = 12)
  AS SELECT id as mover_id,
          nearby->id as unmoved_id
  FROM arrival_raw
  WHERE nearby IS NOT NULL
  PARTITION BY id;

-- this stream can be used for multiple estimation algorithms
CREATE STREAM pickup
  WITH (KAFKA_TOPIC = 'pickup', --infinite retention
        VALUE_FORMAT = 'json',
        PARTITIONS = 12) --high for performant play back
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

CREATE TABLE geohash_avg_estimate_t
  WITH (KAFKA_TOPIC = 'geohash_avg_estimate', --compacted topic --infinite retention
        VALUE_FORMAT = 'json',
        PARTITIONS = 12)
    AS SELECT
      AVG(togo_ms) as togo_ms,
      (mover_geohash + '/' + unmoved_geohash) as hashkey
      FROM pickup
      GROUP BY (mover_geohash + '/' + unmoved_geohash)
      EMIT CHANGES;

-- TODO behavior first time -> without avg'es?
CREATE STREAM homeward
    WITH (KAFKA_TOPIC = 'homeward', --retention period 86400000 ms
          VALUE_FORMAT = 'json',
          PARTITIONS = 12)
    AS SELECT
     trace.unmoved_id as unmoved_id,
     trace.tracking_number AS tracking_number,
     estimate.togo_ms as togo_ms
    FROM trace
    INNER JOIN geohash_avg_estimate_t as estimate ON trace.mover_geohash + '/' + trace.unmoved_geohash = estimate.hashkey
    PARTITION BY trace.unmoved_id;
