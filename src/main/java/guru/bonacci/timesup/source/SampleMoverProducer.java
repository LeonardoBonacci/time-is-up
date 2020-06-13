package guru.bonacci.timesup.source;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import guru.bonacci.timesup.Entities.Mover;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;

public class SampleMoverProducer {

    private static final Logger log = LoggerFactory.getLogger(SampleMoverProducer.class.getName());

    private static final String BOOTSTRAP_SERVERS = "localhost:29092";
    private static final String SCHEMA_REGISTRY = "http://127.0.0.1:8081";
    static final String TOPIC = "movers";

    private final KafkaSender<String, Mover> sender;
    private final SimpleDateFormat dateFormat;

    public SampleMoverProducer(String bootstrapServers) {

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

    public void sendMessages(String topic, CountDownLatch latch) throws InterruptedException {
        sender.send(Flux.interval(Duration.ofSeconds(1))
                        .map(i -> SenderRecord.create(toRecord(topic, StepSimulator.stepMover(i)), i)))
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

    ProducerRecord<String, Mover> toRecord(String topic, Pair<Float, Float> step) {
    	String id = "10";
    	return new ProducerRecord<>(topic, id, Mover.newBuilder().setId(id).setLat(step.getLeft()).setLon(step.getRight()).setUPDATEDTS(System.currentTimeMillis()).build());
    }

    public void close() {
        sender.close();
    }

    public static void main(String[] args) throws Exception {
        int count = 1000;
        CountDownLatch latch = new CountDownLatch(count);
        SampleMoverProducer producer = new SampleMoverProducer(BOOTSTRAP_SERVERS);
        producer.sendMessages(TOPIC, latch);
        latch.await(5, TimeUnit.MINUTES);
        producer.close();
    }
}