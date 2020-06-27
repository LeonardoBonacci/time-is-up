-- query tracks per day per unmoved


-- query self evaluation of estimates

-- query for automatic track delete after pickup
CREATE STREAM countdown
  WITH (KAFKA_TOPIC = 'countdown',
        TIMESTAMP = 'cleanup_time',
        VALUE_FORMAT = 'protobuf',
        PARTITIONS = 1)
  AS SELECT id, tracking_number, rowtime + 120000 AS cleanup_time -- two minutes for testing
  FROM pickup;

-- count_down has messages with future timestamp

CREATE SOURCE CONNECTOR ticker WITH (
    'tasks.max' = '1',
    'connector.class' = 'io.confluent.kafka.connect.datagen.DatagenConnector',
    'kafka.topic' = 'ticker',
    'schema.filename' = '/tmp/ticker.avro',
    'schema.keyfield' = 'tock',
    'key.converter' = 'org.apache.kafka.connect.storage.StringConverter',
    'value.converter' = 'io.confluent.connect.avro.AvroConverter',
    'value.converter.schema.registry.url' = 'http://schema-registry:8081',
    'max.interval' = 1000);
