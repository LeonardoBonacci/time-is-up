# time-is-up


TODO
- add order & pickup topic
- new version tile sink connector


```
CREATE STREAM unmoved
  (
    id VARCHAR,
    name VARCHAR,
    latitude DOUBLE,
    longitude DOUBLE
  )
  WITH (VALUE_FORMAT = 'PROTOBUF', KAFKA_TOPIC = 'unmoved', KEY = 'id', PARTITIONS = 1);

INSERT INTO unmoved (id, name, latitude, longitude) VALUES ('barId', 'some bar', 1.0, 1.0);
```

- SETHOOK arrivals kafka://broker:9092/arrivals NEARBY mover FENCE ROAM unmoved * 50
- KEYS * 
- docker run --net=host -it tile38/tile38 tile38-cli
