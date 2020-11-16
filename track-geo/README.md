./kafka-console-consumer \
     --bootstrap-server localhost:9092 \
     --topic trace \
     --key-deserializer org.apache.kafka.common.serialization.StringDeserializer \
     --value-deserializer org.apache.kafka.connect.json.JsonDeserializer \
     --property print.key=true \
     --from-beginning