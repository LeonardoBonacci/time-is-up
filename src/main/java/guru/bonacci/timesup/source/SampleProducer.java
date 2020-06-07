package guru.bonacci.timesup.source;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
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

public class SampleProducer {

    private static final Logger log = LoggerFactory.getLogger(SampleProducer.class.getName());

    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String SCHEMA_REGISTRY = "http://127.0.0.1:8081";
    static final String TOPIC = "movers";

    private final KafkaSender<String, Mover> sender;
    private final SimpleDateFormat dateFormat;

    public SampleProducer(String bootstrapServers) {

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

    public void sendMessages(String topic, int count, CountDownLatch latch) throws InterruptedException {
        sender.send(Flux.range(1, count)
                        .map(i -> { 
                        	String id = UUID.randomUUID().toString();
                        	Pair<Float, Float> step = step(i);
                        	String loc = step.getLeft() + "," + step.getRight(); 
                        	return SenderRecord.create(new ProducerRecord<>(topic, id, Mover.newBuilder().setId(id).setLOCATION(loc).setUPDATEDTS(System.currentTimeMillis()).build()), i);
                    		}))
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

    public void close() {
        sender.close();
    }

    public static void main(String[] args) throws Exception {
        int count = 20;
        CountDownLatch latch = new CountDownLatch(count);
        SampleProducer producer = new SampleProducer(BOOTSTRAP_SERVERS);
        producer.sendMessages(TOPIC, count, latch);
        latch.await(10, TimeUnit.SECONDS);
        producer.close();
    }
    
    static float fromLat = -36.661281f;
    static float fromLon = 174.743549f;
    static float toLat = -36.801947f;
    static float toLon = 174.758768f;

    static float distanceLat = Math.abs(toLat - fromLat);
    static float distanceLon = Math.abs(toLon - fromLon);

    static float stepLat = distanceLat / 20;;
    static float stepLon = distanceLon / 20;;

    Pair<Float, Float> step(int count) {
    	return Pair.of(fromLat + count*stepLat, fromLon + count*stepLon);
    }

}