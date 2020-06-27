-- query tracks per day per unmoved


-- query self evaluation of estimates

-- log.cleaner.threads = 10
-- use LogAppendTime
-- query for automatic track delete after pickup
CREATE STREAM count_down
  WITH (KAFKA_TOPIC = 'count_down',
        TIMESTAMP = 'cleanup_time',
        VALUE_FORMAT = 'protobuf',
        PARTITIONS = 1)
  AS SELECT id, tracking_number, rowtime + 180000 AS cleanup_time -- add three minutes for testing
  FROM pickup;

-- count_down has messages with future timestamp
-- use LogAppendTime
-- stopwach datagen connector
-- kstream-app join with time window within 3 minutes
-- inserts tombstone messages in track

CREATE SOURCE CONNECTOR ticker WITH (
    'tasks.max' = '1',
    'connector.class' = 'io.confluent.kafka.connect.datagen.DatagenConnector',
    'kafka.topic' = 'ticker',
    'schema.filename' = '/tmp/ticker.avro',
    'schema.keyfield' = 'tock',
    'key.converter' = 'org.apache.kafka.connect.storage.StringConverter',
    'value.converter' = 'org.apache.kafka.connect.storage.StringConverter',
    'max.interval' = 1000);
