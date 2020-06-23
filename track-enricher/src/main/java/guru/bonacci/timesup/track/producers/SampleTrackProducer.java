package guru.bonacci.timesup.track.producers;

import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import guru.bonacci.timesup.model.Track.ConnectDefault1;


public class SampleTrackProducer {

	static final String TOPIC = "track";
	
	Producer<String, ConnectDefault1> producer;


	public static void main(final String[] args) {
		new SampleTrackProducer().send();
	}
	
	public SampleTrackProducer() {
		producer = new KafkaProducer<>(configure());
	}

	private Properties configure() {
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
       		  "io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer");
		props.put(ProducerConfig.ACKS_CONFIG, "all");

		props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);
		props.put(ProducerConfig.CLIENT_ID_CONFIG, "i am a client");
		
		props.put("schema.registry.url", "http://localhost:8081");
		return props;
	}

	// Produce sample data
	void send() {
		String key = "thisisme";
		ConnectDefault1 record = ConnectDefault1.newBuilder().setTRACKINGNUMBER("someLongNumber").setMOVERID("moverid2").setUNMOVEDID("Torpedo7Albany").build();

		System.out.printf("Producing record: %s\t%s", key, record);
		producer.send(new ProducerRecord<String, ConnectDefault1>(TOPIC, key, record), new Callback() {
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