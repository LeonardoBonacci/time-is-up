package guru.bonacci.timesup.tracefilter;

import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import guru.bonacci.timesup.tracefilter.model.Track;
import io.quarkus.kafka.client.serialization.JsonbSerializer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TrackProducer {

	Producer<String, Track> producer;

	public static void main(final String[] args) {
		TrackProducer traceProducer = new TrackProducer();
		traceProducer.send();
	}

	public TrackProducer() {
		producer = new KafkaProducer<>(configure());
	}

	private Properties configure() {
		final Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonbSerializer.class.getName());
		props.put(ProducerConfig.CLIENT_ID_CONFIG, "i-produce-traces-on-demand");
		return props;
	}

	void send() {
		for (int i=0; i<5; i++) {
			Track record = Track.builder()
					.moverId("mover" + i)
					.trackingNumber("tracking number" + i)
					.unmovedId("unmoved " + i)
					.build();

			String key = record.getTrackingNumber();

			log.info("sending trace {}", record);
			producer.send(new ProducerRecord<>(TopologyProducer.TRACK_TOPIC, key, record), new Callback() {
				@Override
				public void onCompletion(RecordMetadata m, Exception e) {
					if (e != null) {
						e.printStackTrace();
					} else {
						log.info("Produced record to topic {} partition [{}] @ offset {}", 
									m.topic(), m.partition(), m.offset());
					}
				}
			});
		}	
		
		producer.flush();
		log.info("voila");
		producer.close();
	}
}
