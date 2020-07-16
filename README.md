# time-is-up

curl -sX POST http://localhost:8080/unmoved -d @payloads\mover.json --header "Content-Type: application/json" | jq
curl -sX POST http://localhost:8080/unmoved -d @payloads\unmoved.json --header "Content-Type: application/json" | jq
curl -sX POST http://localhost:8080/track -d @payloads\track.json --header "Content-Type: application/json" | jq

curl -X DELETE -H "Content-type: application/json" http://localhost:8080/unmoved/Torpedo7Albany | jq
curl -X DELETE -H "Content-type: application/json" http://localhost:8080/track/foo | jq



- fix bug connector expire
- deploy native containers to Fargate

- https://www.confluent.io/blog/importance-of-distributed-tracing-for-apache-kafka-based-applications/
- https://github.com/openzipkin-contrib/brave-kafka-interceptor

- tracing zipkin
- deploy Kafka to AWS after KIP-500
- in AWS: ELK
- producers can write straight to ksql-streams

- linear regression?
