package guru.bonacci.timesup.track.producers;

import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import guru.bonacci.timesup.model.UnmovedGeo;
import io.confluent.kafka.serializers.KafkaAvroSerializer;

public class SampleUnmovedGeoProducer {

	static final String TOPIC = "unmoved_geo";
	
	Producer<String, UnmovedGeo> producer;


	public static void main(final String[] args) {
		new SampleUnmovedGeoProducer().send();
	}
	
	public SampleUnmovedGeoProducer() {
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
		String key = "Torpedo7Albany";
		UnmovedGeo record = UnmovedGeo.newBuilder().setId(key).setLat(1.0f).setLon(2.0f).setGeohash("s00twy01").build();
//		record = null; //tombstone
		
		System.out.printf("Producing record: %s\t%s", key, record);
		producer.send(new ProducerRecord<String, UnmovedGeo>(TOPIC, key, record), new Callback() {
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
}