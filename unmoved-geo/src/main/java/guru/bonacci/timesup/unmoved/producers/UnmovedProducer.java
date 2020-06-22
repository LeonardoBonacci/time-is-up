package guru.bonacci.timesup.unmoved.producers;

import java.text.SimpleDateFormat;
import java.time.Duration;
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

import guru.bonacci.timesup.model.TheUnmoved.Unmoved;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;

public class UnmovedProducer {

    private static final Logger log = LoggerFactory.getLogger(UnmovedProducer.class.getName());

    static final String BOOTSTRAP_SERVERS = "localhost:9092";
    static final String SCHEMA_REGISTRY = "http://127.0.0.1:8081";
    static final String TOPIC = "unmoved-topic";

    private final KafkaSender<String, Unmoved> sender;
    private final SimpleDateFormat dateFormat;

    public UnmovedProducer(String bootstrapServers) {

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

//	String loc = step.getLeft() + "," + step.getRight(); 
    public void sendMessages(String topic, CountDownLatch latch) throws InterruptedException {
        sender.send(Flux.interval(Duration.ofSeconds(1))
                        .map(i -> SenderRecord.create(toRecord(topic), i)))
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

    ProducerRecord<String, Unmoved> toRecord(String topic) {
    	String id = "bar2";
    	Unmoved record = Unmoved.newBuilder().setId(id).setLatitude(1.0f).setLongitude(1.0f).build();
//    	record = null;
    	return new ProducerRecord<>(topic, id, record);
    }

    public void close() {
        sender.close();
    }

    public static void main(String[] args) throws Exception {
        int count = 1;
        CountDownLatch latch = new CountDownLatch(count);
        UnmovedProducer producer = new UnmovedProducer(BOOTSTRAP_SERVERS);
        producer.sendMessages(TOPIC, latch);
        latch.await(5, TimeUnit.MINUTES);
        producer.close();
    }
}