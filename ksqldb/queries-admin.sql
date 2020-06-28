-- query tracks per day per unmoved


-- query self evaluation of estimates


-- queries for automatic track delete after pickup


CREATE TABLE track_t
    (rowkey VARCHAR KEY,
     tracking_number VARCHAR,
     mover_id VARCHAR,
     unmoved_id VARCHAR)
  WITH (KAFKA_TOPIC = 'track',
       VALUE_FORMAT = 'avro',
       VALUE_AVRO_SCHEMA_FULL_NAME='guru.bonacci.timesup.model.Track',
       KEY = 'tracking_number',
       PARTITIONS = 1);


CREATE STREAM track_geo
  (tracking_number VARCHAR,
     mover_id VARCHAR,
     unmoved_id VARCHAR)
  WITH (KAFKA_TOPIC = 'track',
        KEY = 'mover_id',
        VALUE_FORMAT = 'avro',
        VALUE_AVRO_SCHEMA_FULL_NAME='guru.bonacci.timesup.model.TrackGeo',
        PARTITIONS = 1);


CREATE STREAM arrival
   (mover_id VARCHAR,
    unmoved_id VARCHAR,
    answer VARCHAR)
   WITH (KAFKA_TOPIC = 'arrival',
         KEY = 'mover_id',
         VALUE_FORMAT = 'avro',
         VALUE_AVRO_SCHEMA_FULL_NAME='guru.bonacci.timesup.model.Arrival',
         PARTITIONS = 1);

INSERT INTO arrival (mover_id, unmoved_id, answer) VALUES ('moverid', 'Torpedo7Albany', '42');

--todo stream table join
CREATE STREAM countdown
   WITH (VALUE_FORMAT = 'avro',
       VALUE_AVRO_SCHEMA_FULL_NAME='guru.bonacci.timesup.model.Countdown',
       KAFKA_TOPIC = 'countdown',
       TIMESTAMP = 'cleanup_time',
       PARTITIONS = 1)
   AS SELECT
     track.tracking_number AS tracking_number,
     arrival.answer AS answer,
     arrival.rowtime + 120000 AS cleanup_time
   FROM track_geo AS track
   INNER JOIN arrival as arrival WITHIN 1 HOUR ON arrival.mover_id = track.mover_id
   WHERE arrival.unmoved_id = track.unmoved_id
   PARTITION BY answer;

-- CREATE STREAM countdown
--   WITH (KAFKA_TOPIC = 'countdown',
--         TIMESTAMP = 'cleanup_time',
--         VALUE_FORMAT = 'protobuf',
--         PARTITIONS = 1)
--   AS SELECT id, tracking_number, rowtime + 120000 AS cleanup_time -- two minutes for testing
--   FROM pickup;

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
