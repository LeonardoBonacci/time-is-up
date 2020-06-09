# time-is-up


TODO
- KSQLDB -> add UDF to sink geopoint 
- make timestamp work
- Tile38 connector -> add timout option
- add orders topic

curl -sX PUT http://localhost:9200/_template/kafkaconnect -d @./elastic/template.json --header "Content-Type: application/json" | jq

curl -sX POST http://localhost:8083/connectors -d @connectors/es-sink.json --header "Content-Type: application/json" | jq

curl localhost:8083/connectors | jq

curl localhost:8083/connectors/es-sink/status | jq

curl -X DELETE -H "Content-type: application/json" http://localhost:8083/connectors/es-sink | jq


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
  
  http://localhost:9200/moveon/_search?pretty=true&q=*:*
  
  http://localhost:9200/moveon/_mapping?pretty=true