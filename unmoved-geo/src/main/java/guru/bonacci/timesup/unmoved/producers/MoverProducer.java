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

import guru.bonacci.timesup.model.TheMover.Mover;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;

public class MoverProducer {

    private static final Logger log = LoggerFactory.getLogger(MoverProducer.class.getName());

    static final String BOOTSTRAP_SERVERS = "localhost:29092";
    static final String SCHEMA_REGISTRY = "http://127.0.0.1:8081";
    static final String TOPIC = "testmover";

    private final KafkaSender<String, Mover> sender;
    private final SimpleDateFormat dateFormat;

    public MoverProducer(String bootstrapServers) {

        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "sample-producer");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
        		  "io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer");
		props.put("schema.registry.url", SCHEMA_REGISTRY);
		SenderOptions<String, Mover> senderOptions = SenderOptions.create(props);

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

    ProducerRecord<String, Mover> toRecord(String topic) {
    	Mover record = Mover.newBuilder().setId("foo").setLat(1.1f).setLon(1.0f).build();
//    	record = null;
    	return new ProducerRecord<>(topic, "foo", record);
    }

    public void close() {
        sender.close();
    }

    public static void main(String[] args) throws Exception {
        int count = 1;
        CountDownLatch latch = new CountDownLatch(count);
        MoverProducer producer = new MoverProducer(BOOTSTRAP_SERVERS);
        producer.sendMessages(TOPIC, latch);
        latch.await(5, TimeUnit.MINUTES);
        producer.close();
    }
}