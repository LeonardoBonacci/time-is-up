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
  name VARCHAR,
  latitude DOUBLE,
  longitude DOUBLE)
 WITH (KAFKA_TOPIC = 'unmoved',
       VALUE_FORMAT = 'protobuf',
       KEY = 'id',
       PARTITIONS = 1);

INSERT INTO unmoved (id, name, latitude, longitude) VALUES ('unmovedId', 'some place', 1.0, 1.0);

CREATE TABLE track
 (rowkey VARCHAR KEY,
   id VARCHAR,
   mover_id VARCHAR,
   unmoved_id VARCHAR)
WITH (KAFKA_TOPIC = 'track',
     VALUE_FORMAT = 'protobuf',
     KEY = 'mover_id',
     PARTITIONS = 1);

INSERT INTO track (rowkey, id, mover_id, unmoved_id) VALUES ('moverId', 'trackId', 'moverId', 'unmovedId');

CREATE STREAM trace
  WITH (VALUE_FORMAT = 'protobuf',
        KAFKA_TOPIC = 'trace',
        PARTITIONS = 1)
  AS SELECT mover.id AS id, mover.lat AS lat, mover.lon AS lon, track.id AS track_id
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
  'tile38.topic.trace' = 'SET trace event.ID POINT event.LAT event.LON',
  'tile38.host' = 'tile38',
  'tile38.port' = 9851,
  'errors.tolerance' = 'all',
  'errors.log.enable' = true,
  'errors.log.include.messages' = true);

  CREATE STREAM arrival (id STRING)
  WITH (KAFKA_TOPIC='arrival', VALUE_FORMAT='JSON');


CREATE STREAM arrivals (id STRING, faraway STRUCT<id STRING>) WITH (KAFKA_TOPIC='arrival', VALUE_FORMAT='JSON');

CREATE STREAM arrivals3  AS SELECT id as mover_id, faraway->id as unmoved_id FROM arrivals WHERE faraway IS NOT NULL PARTITION BY id;

SELECT arrivals3.moved_id AS id
  FROM trace
  INNER JOIN arrivals3 WITHIN 2 HOURS ON arrivals3.moved_id = trace.id
  WHERE arrivals3.moved_id = trace.id
  EMIT CHANGES;

SELECT arrivals3.moved_id AS id
  FROM trace
  INNER JOIN arrivals3 WITHIN 2 HOURS ON arrivals3.moved_id = trace.id
  WHERE arrivals3.unmoved_id = trace.unmoved_id
  EMIT CHANGES;
