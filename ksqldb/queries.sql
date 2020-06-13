CREATE SINK CONNECTOR tile38_sink WITH (
  'tasks.max' = '1',
  'connector.class' = 'guru.bonacci.kafka.connect.tile38.Tile38SinkConnector',
  'topics' = 'movers',
  'key.converter' = 'org.apache.kafka.connect.storage.StringConverter',
  'value.converter' = 'io.confluent.connect.protobuf.ProtobufConverter',
  'value.converter.schema.registry.url' = 'http://schema-registry:8081',
  'tile38.topic.movers' = 'SET mover event.id POINT event.lat event.lon',
  'tile38.host' = 'tile38',
  'tile38.port' = 9851,
  'errors.tolerance' = 'all',
  'errors.log.enable' = true,
  'errors.log.include.messages' = true);



-- elasticsearch sink
CREATE STREAM moveon
  WITH (KAFKA_TOPIC='moveon',
        VALUE_FORMAT='PROTOBUF') AS
SELECT CAST(lat AS STRING) + ',' + CAST(lon AS STRING) AS LOCATION
FROM  movers;

CREATE SINK CONNECTOR es_sink WITH (
  'tasks.max' = '1',
  'connector.class' = 'io.confluent.connect.elasticsearch.ElasticsearchSinkConnector',
  'connection.url' = 'http://es:9200',
  'topics' = 'moveon',
  'type.name' = '',
  'schema.ignore' = true,
  'key.converter' = 'org.apache.kafka.connect.storage.StringConverter',
  'value.converter' = 'io.confluent.connect.protobuf.ProtobufConverter',
  'value.converter.schema.registry.url' = 'http://schema-registry:8081',
  'errors.tolerance' = 'all',
  'errors.log.enable' = true,
  'errors.log.include.messages' = true,
  'behavior.on.malformed.documents' = 'warn');
