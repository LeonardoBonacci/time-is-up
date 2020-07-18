# time-is-up

mvn package -Pnative -Dquarkus.profile=docker -Dquarkus.native.container-build=true
docker build -f src/main/docker/Dockerfile.native -t leonardobonacci/timestup-track-gate:1.0 .
docker push leonardobonacci/timesup-track-gate:1.0

./kafka-console-consumer \
     --bootstrap-server localhost:9092 \
     --topic trace \
     --key-deserializer org.apache.kafka.common.serialization.StringDeserializer \
     --value-deserializer org.apache.kafka.connect.json.JsonDeserializer \
     --property print.key=true \
     --from-beginning

curl -i -sX POST http://localhost:8082/unmoved -d @payloads\unmoved.json --header "Content-Type: application/json"
curl -i -sX POST http://localhost:8083/track -d @payloads\track.json --header "Content-Type: application/json"
curl -i -sX POST http://localhost:8081/mover -d @payloads\mover.json --header "Content-Type: application/json"

curl -X DELETE -H "Content-type: application/json" http://localhost:8082/unmoved/Torpedo7Albany | jq
curl -X DELETE -H "Content-type: application/json" http://localhost:8083/track/foo | jq

curl -X GET -H "Content-type: application/json" http://localhost:8080/home/Torpedo7Albany | jq

docker-compose up -d --scale home=3

docker run --tty --rm -i --network tn debezium/tooling:1.0
http home:8080/home/meta-data
http a1ae0c7a5cd9:8080/home/Torpedo7Albany
http --follow 2af13fe516a9:8080/home/Torpedo7Albany

- deploy native containers to Fargate
- https://www.confluent.io/blog/importance-of-distributed-tracing-for-apache-kafka-based-applications/
- https://github.com/openzipkin-contrib/brave-kafka-interceptor
- tracing zipkin
- deploy Kafka to AWS after KIP-500
