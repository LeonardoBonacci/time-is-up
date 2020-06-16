-- first generate some data with sample producer

CREATE STREAM mover
  (id VARCHAR,
   lat DOUBLE,
   lon DOUBLE)
  WITH (KAFKA_TOPIC='mover',
        VALUE_FORMAT='protobuf',
        KEY = 'id');

CREATE STREAM unmoved
 (id VARCHAR,
  latitude DOUBLE,
  longitude DOUBLE)
 WITH (KAFKA_TOPIC = 'unmoved',
       VALUE_FORMAT = 'protobuf',
       KEY = 'id',
       PARTITIONS = 1);

INSERT INTO unmoved (id, latitude, longitude) VALUES ('Torpedo7Albany', 1.0, 1.0);

CREATE TABLE track
  (rowkey VARCHAR KEY,
   tracking_number VARCHAR,
   mover_id VARCHAR,
   unmoved_id VARCHAR)
WITH (KAFKA_TOPIC = 'track',
     VALUE_FORMAT = 'protobuf',
     KEY = 'mover_id',
     PARTITIONS = 1);

INSERT INTO track (rowkey, tracking_number, mover_id, unmoved_id) VALUES ('me', '3SABC1234567890', 'me', 'Torpedo7Albany');

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
  INNER JOIN track ON mover.id = track.mover_id
  PARTITION BY mover.id
  EMIT CHANGES;

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

CREATE STREAM tile_arrival (id STRING, nearby STRUCT<id STRING>)
  WITH (KAFKA_TOPIC = 'tile_arrival',
        VALUE_FORMAT='json');

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
SELECT trace.id as mover_id,
		trace.lat as lat,
        trace.lon as lon,
        trace.tracking_number as trackingnumber,
        arrival.unmoved_id as unmoved_id,
        arrival.rowtime - trace.rowtime as togo
  FROM trace
  INNER JOIN arrival WITHIN (0 MILLISECONDS, 1 HOUR) ON arrival.mover_id = trace.id
  WHERE arrival.unmoved_id = trace.unmoved_id
  EMIT CHANGES;
