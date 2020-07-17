# time-is-up

curl -i -sX POST http://localhost:8081/mover -d @payloads\mover.json --header "Content-Type: application/json" | jq
curl -i -sX POST http://localhost:8082/unmoved -d @payloads\unmoved.json --header "Content-Type: application/json" | jq
curl -i -sX POST http://localhost:8083/track -d @payloads\track.json --header "Content-Type: application/json" | jq

curl -X DELETE -H "Content-type: application/json" http://localhost:8082/unmoved/Torpedo7Albany | jq
curl -X DELETE -H "Content-type: application/json" http://localhost:8083/track/foo | jq

curl -X GET -H "Content-type: application/json" http://localhost:8080/home/Torpedo7Albany | jq

docker-compose up -d --scale home=3

docker run --tty --rm -i --network kn debezium/tooling:1.0
http home:8080/home/meta-data
http 2af13fe516a9:8080/home/Torpedo7Albany
http --follow 2af13fe516a9:8080/home/Torpedo7Albany

- deploy native containers to Fargate
- https://www.confluent.io/blog/importance-of-distributed-tracing-for-apache-kafka-based-applications/
- https://github.com/openzipkin-contrib/brave-kafka-interceptor
- tracing zipkin
- deploy Kafka to AWS after KIP-500
