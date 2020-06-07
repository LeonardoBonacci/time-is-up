# time-is-up

TODO
- simulate messages from home to work
- KSQLDB -> add UDF to sink geopoint

curl -sX POST http://localhost:8083/connectors -d @connectors/es-sink.json --header "Content-Type: application/json" | jq

curl localhost:8083/connectors | jq

curl localhost:8083/connectors/es-sink/status | jq

curl -X DELETE -H "Content-type: application/json" http://localhost:8083/connectors/es-sink | jq
