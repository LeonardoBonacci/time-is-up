package guru.bonacci.timesup.source;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import guru.bonacci.timesup.Entities.Unmoved;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;

public class SampleUnmovedProducer {

    private static final Logger log = LoggerFactory.getLogger(SampleUnmovedProducer.class.getName());

    private static final String BOOTSTRAP_SERVERS = "localhost:29092";
    private static final String SCHEMA_REGISTRY = "http://127.0.0.1:8081";
    static final String TOPIC = "unmoved";

    private final KafkaSender<String, Unmoved> sender;
    private final SimpleDateFormat dateFormat;

    public SampleUnmovedProducer(String bootstrapServers) {

        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "sample-producer");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
        		  "io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer");
		props.put("schema.registry.url", SCHEMA_REGISTRY);
		SenderOptions<String, Unmoved> senderOptions = SenderOptions.create(props);

        sender = KafkaSender.create(senderOptions);
        dateFormat = new SimpleDateFormat("HH:mm:ss:SSS z dd MMM yyyy");
    }

    public void sendMessages(String topic, int count, CountDownLatch latch) throws InterruptedException {
        sender.send(Flux.range(1, count)
        				.map(i -> SenderRecord.create(toRecord(topic, i, 0.0f, 0.0f), i)))
              .doOnError(e -> log.error("Send failed", e))
              .subscribe(r -> {
                  RecordMetadata metadata = r.recordMetadata();
                  System.out.printf("Message %d sent successfully, topic-partition=%s-%d offset=%d timestamp=%s\n",
                      r.correlationMetadata(),
                      metadata.topic(),
                      metadata.partition(),
                      metadata.offset(),
                      dateFormat.format(new Date(metadata.timestamp())));
                  latch.countDown();
              });
    }

    ProducerRecord<String, Unmoved> toRecord(String topic, int i, Float lat, Float lon) {
    	String id = ""+i;
    	String name = "unmoved"+id;
    	return new ProducerRecord<>(topic, id, Unmoved.newBuilder().setId(id).setName(name).setLat(lat).setLon(lon).build());
    }

    public void close() {
        sender.close();
    }

    public static void main(String[] args) throws Exception {
        int count = 5;
        CountDownLatch latch = new CountDownLatch(count);
        SampleUnmovedProducer producer = new SampleUnmovedProducer(BOOTSTRAP_SERVERS);
        producer.sendMessages(TOPIC, count, latch);
        latch.await(10, TimeUnit.SECONDS);
        producer.close();
    }
}