CREATE STREAM mover2
  (id VARCHAR,
   lat DOUBLE,
   lon DOUBLE)
  WITH (KAFKA_TOPIC='mover_t1',
        VALUE_FORMAT='PROTOBUF',
        KEY = 'id');


CREATE STREAM unmoved
  (id VARCHAR,
   name VARCHAR,
   latitude DOUBLE,
   longitude DOUBLE)
  WITH (KAFKA_TOPIC='unmoved_t1',
        VALUE_FORMAT='PROTOBUF',
        KEY = 'id');

CREATE SINK CONNECTOR tile38_sink WITH (
  'tasks.max' = '1',
  'connector.class' = 'guru.bonacci.kafka.connect.tile38.Tile38SinkConnector',
  'topics' = 'unmoved_t1,mover_t1',
  'key.converter' = 'org.apache.kafka.connect.storage.StringConverter',
  'value.converter' = 'io.confluent.connect.protobuf.ProtobufConverter',
  'value.converter.schema.registry.url' = 'http://schema-registry:8081',
  'tile38.topic.unmoved_t1' = 'SET unmoved event.id POINT event.latitude event.longitude',
  'tile38.topic.mover_t1' = 'SET mover event.id POINT event.lat event.lon',
  'tile38.host' = 'tile38',
  'tile38.port' = 9851,
  'errors.tolerance' = 'all',
  'errors.log.enable' = true,
  'errors.log.include.messages' = true);
