# time-is-up

curl -i -sX POST http://localhost:8081/mover -d @payloads\mover.json --header "Content-Type: application/json" | jq
curl -i -sX POST http://localhost:8082/unmoved -d @payloads\unmoved.json --header "Content-Type: application/json" | jq
curl -i -sX POST http://localhost:8083/track -d @payloads\track.json --header "Content-Type: application/json" | jq

curl -X DELETE -H "Content-type: application/json" http://localhost:8082/unmoved/Torpedo7Albany | jq
curl -X DELETE -H "Content-type: application/json" http://localhost:8083/track/foo | jq



- fix bug connector expire
- deploy native containers to Fargate

- https://www.confluent.io/blog/importance-of-distributed-tracing-for-apache-kafka-based-applications/
- https://github.com/openzipkin-contrib/brave-kafka-interceptor

- tracing zipkin
- deploy Kafka to AWS after KIP-500
- in AWS: ELK
- linear regression?
