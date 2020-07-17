CREATE STREAM pickup
 (tracking_number VARCHAR)
 WITH (KAFKA_TOPIC = 'pickup',
       VALUE_FORMAT = 'json',
       PARTITIONS = 1);


INSERT INTO pickup (tracking_number) VALUES ('foo');


CREATE STREAM homeward
 (rowkey VARCHAR KEY,
  unmoved_id VARCHAR,
  tracking_number VARCHAR,
  togo_ms INT)
 WITH (KAFKA_TOPIC = 'homeward',
       VALUE_FORMAT = 'json',
       PARTITIONS = 12);

INSERT INTO homeward (rowkey, unmoved_id, tracking_number, togo_ms) VALUES ('Torpedo7Albany', 'Torpedo7Albany', 'order1', 10);
INSERT INTO homeward (rowkey, unmoved_id, tracking_number, togo_ms) VALUES ('Torpedo7Albany', 'Torpedo7Albany', 'order2', 15);
INSERT INTO homeward (rowkey, unmoved_id, tracking_number, togo_ms) VALUES ('foo', 'foo', 'bar', 100);
INSERT INTO homeward (rowkey, unmoved_id, tracking_number, togo_ms) VALUES ('Torpedo7Albany', 'Torpedo7Albany', 'order1', 5);
INSERT INTO homeward (rowkey, unmoved_id, tracking_number, togo_ms) VALUES ('Torpedo7Albany', 'Torpedo7Albany', 'order2', 2);
