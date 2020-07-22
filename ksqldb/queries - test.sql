INSERT INTO unmoved (rowkey, id, lat, lon) VALUES ('Torpedo7Albany', 'Torpedo7Albany', 1.0, 1.0);
INSERT INTO unmoved (rowkey, id, lat, lon) VALUES ('Torpedo7Albany', 'TEST', 10.0, -10.0);
INSERT INTO track (tracking_number, mover_id, unmoved_id) VALUES ('somenumber', 'thisisme', 'Torpedo7Albany');
INSERT INTO mover (id, lat, lon) VALUES ('thisisme', 0.90, 0.90);
INSERT INTO mover (id, lat, lon) VALUES ('thisisme', 0.71, 0.96);
INSERT INTO mover (id, lat, lon) VALUES ('thisisme', 1.0, 1.0);


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



CREATE STREAM mover
  (id VARCHAR KEY,
   lat DOUBLE,
   lon DOUBLE)
  WITH (KAFKA_TOPIC='mover', --retention period 86400000 ms
        VALUE_FORMAT='json',
        PARTITIONS = 12);
