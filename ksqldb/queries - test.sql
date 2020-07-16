CREATE STREAM pickup
 (tracking_number VARCHAR)
 WITH (KAFKA_TOPIC = 'pickup',
       VALUE_FORMAT = 'json',
       PARTITIONS = 1);


INSERT INTO pickup (tracking_number) VALUES ('foo');
