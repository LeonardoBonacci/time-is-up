package guru.bonacci.foo.reactive;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import guru.bonacci.foo.Movers.Mover;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOffset;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;

public class FooConsumer {

    private static final Logger log = LoggerFactory.getLogger(FooConsumer.class.getName());

    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String SCHEMA_REGISTRY = "http://127.0.0.1:8081";
    private static final String TOPIC = "mover";

    private final ReceiverOptions<Integer, Mover> receiverOptions;
    private final SimpleDateFormat dateFormat;

    public FooConsumer(String bootstrapServers) {

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "sample-consumerss");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "sample-groupss");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
      		  "io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializer");
		props.put("schema.registry.url", SCHEMA_REGISTRY);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        receiverOptions = ReceiverOptions.create(props);
        dateFormat = new SimpleDateFormat("HH:mm:ss:SSS z dd MMM yyyy");
    }

    public Disposable consumeMessages(String topic, CountDownLatch latch) {

        ReceiverOptions<Integer, Mover> options = receiverOptions.subscription(Collections.singleton(topic))
                .addAssignListener(partitions -> log.debug("onPartitionsAssigned {}", partitions))
                .addRevokeListener(partitions -> log.debug("onPartitionsRevoked {}", partitions));
        Flux<ReceiverRecord<Integer, Mover>> kafkaFlux = KafkaReceiver.create(options).receive();
        return kafkaFlux.subscribe(record -> {
            ReceiverOffset offset = record.receiverOffset();
            System.out.printf("Received message: topic-partition=%s offset=%d timestamp=%s key=%d value=%s\n",
                    offset.topicPartition(),
                    offset.offset(),
                    dateFormat.format(new Date(record.timestamp())),
                    record.key(),
                    record.value());
            offset.acknowledge();
            latch.countDown();
        });
    }

    public static void main(String[] args) throws Exception {
        int count = 20;
        CountDownLatch latch = new CountDownLatch(count);
        FooConsumer consumer = new FooConsumer(BOOTSTRAP_SERVERS);
        Disposable disposable = consumer.consumeMessages(TOPIC, latch);
        latch.await(10, TimeUnit.SECONDS);
        disposable.dispose();
    }
}