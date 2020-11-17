package guru.bonacci.timesup.pickup;

import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import guru.bonacci.timesup.pickup.model.Trace;
import io.quarkus.kafka.client.serialization.JsonbSerializer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TraceProducer {

	Producer<String, Trace> producer;

	public static void main(final String[] args) {
		TraceProducer traceProducer = new TraceProducer();
		traceProducer.send();
	}

	public TraceProducer() {
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
		for (int i=0; i<10; i++) {
			Trace record = Trace.builder()
					.moverId("movers-" + i)
					.moverGeohash("hhhhaaassshh")
					.moverLat(12.34)
					.moverLon(44.44)
					.trackingNumber("tracking numberss" + i)
					.unmovedGeohash("xxx")
					.unmovedId("unmoved" + i)
					.unmovedLat(98.65)
					.unmovedLon(98.65)
					.build();

			String key = record.moverId;

			log.info("sending trace {}", record);
			producer.send(new ProducerRecord<>(PickupTopology.TRACE_TOPIC, key, record), new Callback() {
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
