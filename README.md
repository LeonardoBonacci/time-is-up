# time-is-up


TODO
- sink to Tile38
- KSQLDB -> add UDF to sink geopoint 
- make timestamp work
- add orders topic

docker exec -it ksqldb-server curl localhost:8083/connectors | jq
docker run --net=host -it tile38/tile38 tile38-cli

-----------------------------------ES----------------------------------------------
curl -sX PUT http://localhost:9200/_template/kafkaconnect -d @./elastic/template.json --header "Content-Type: application/json" | jq

curl -sX POST http://localhost:8083/connectors -d @connectors/es-sink.json --header "Content-Type: application/json" | jq

http://localhost:9200/moveon/_search?pretty=true&q=*:*
 
http://localhost:9200/moveon/_mapping?pretty=true