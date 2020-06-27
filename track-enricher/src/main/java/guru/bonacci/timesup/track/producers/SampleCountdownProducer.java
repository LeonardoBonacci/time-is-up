package guru.bonacci.timesup.track.producers;

import java.util.Arrays;
import java.util.Properties;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.reflect.ReflectData;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import lombok.Builder;


public class SampleCountdownProducer {

	static final String TOPIC = "countdown";
	
	Producer<String, GenericRecord> producer;


	public static void main(final String[] args) {
		new SampleCountdownProducer().send();
	}
	
	public SampleCountdownProducer() {
		producer = new KafkaProducer<>(configure());
	}

	private Properties configure() {
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
		props.put(ProducerConfig.ACKS_CONFIG, "all");

		props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);
		props.put(ProducerConfig.CLIENT_ID_CONFIG, "i am a client");
		
		props.put("schema.registry.url", "http://localhost:8081");
		return props;
	}

	// Produce sample data
	void send() {
		String key = "42";
		Countdown cd = Countdown.builder().answer_to_life(key).tracking_number("someLongNumber").build();
		GenericRecord record = createFrom(cd);
		
		System.out.printf("Producing record: %s\t%s", key, record);
		producer.send(new ProducerRecord<String, GenericRecord>(TOPIC, key, record), new Callback() {
			@Override
			public void onCompletion(RecordMetadata m, Exception e) {
				if (e != null) {
					e.printStackTrace();
				} else {
					System.out.printf("Produced record to topic %s partition [%d] @ offset %d%n", m.topic(),
							m.partition(), m.offset());
				}
			}
		});

		producer.flush();
		System.out.printf("message sent to topic %s%n", TOPIC);
		producer.close();
	}

	private static GenericRecord createFrom(Object object) {
		final Schema schema = ReflectData.get().getSchema(object.getClass());
        final GenericData.Record record = new GenericData.Record(schema);
        Arrays.stream(object.getClass().getDeclaredFields())
        .forEach(field -> {
	        try {
	            record.put(field.getName(), field.get(object));
	        } catch (IllegalAccessException e) {
	            e.printStackTrace();
	            }
	        });
	        return record;
	    }

	@Builder
	@SuppressWarnings("unused")
	private static class Countdown {
		String answer_to_life;
		String tracking_number;
    }
}