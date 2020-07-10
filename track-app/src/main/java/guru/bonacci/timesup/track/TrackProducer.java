package guru.bonacci.timesup.track;

import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import guru.bonacci.timesup.track.model.Track;
import io.confluent.kafka.serializers.KafkaJsonSerializer;

@ApplicationScoped
public class TrackProducer {

	static final String TOPIC = "track";
	
	Producer<String, Track> producer;

	public TrackProducer() {
		producer = new KafkaProducer<>(configure());
	}

	private Properties configure() {
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "broker:29092");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaJsonSerializer.class.getName());
		props.put(ProducerConfig.ACKS_CONFIG, "all");

		props.put(ProducerConfig.CLIENT_ID_CONFIG, "the-track-client");
		return props;
	}

	void send(Track record) {
		final String key = record.tracking_number;
		System.out.printf("Producing record: %s\t%s%n", key, record);

		producer.send(new ProducerRecord<String, Track>(TOPIC, key, record), new Callback() {
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
	}
}
