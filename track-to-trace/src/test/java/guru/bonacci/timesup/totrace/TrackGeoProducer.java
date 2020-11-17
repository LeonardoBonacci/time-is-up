package guru.bonacci.timesup.totrace;

import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import guru.bonacci.timesup.totrace.model.TrackGeo;
import io.quarkus.kafka.client.serialization.JsonbSerializer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TrackGeoProducer {

	Producer<String, TrackGeo> producer;

	public static void main(final String[] args) {
		TrackGeoProducer traceProducer = new TrackGeoProducer();
		traceProducer.send();
	}

	public TrackGeoProducer() {
		producer = new KafkaProducer<>(configure());
	}

	private Properties configure() {
		final Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonbSerializer.class.getName());
		props.put(ProducerConfig.CLIENT_ID_CONFIG, "i-produce-track-geos-on-demand");
		return props;
	}

	void send() {
		for (int i=0; i<5; i++) {
			TrackGeo record = TrackGeo.builder()
					.moverId("mover-" + i)
					.trackingNumber("tracking numberss" + i)
					.unmovedGeohash("xxx")
					.unmovedId("unmoved" + i)
					.unmovedLat(98.65)
					.unmovedLon(98.65)
					.build();

			String key = record.trackingNumber;

			log.info("sending trace {}", record);
			producer.send(new ProducerRecord<>(TrackToTraceTopology.TRACK_GEO_TOPIC, key, record), new Callback() {
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
