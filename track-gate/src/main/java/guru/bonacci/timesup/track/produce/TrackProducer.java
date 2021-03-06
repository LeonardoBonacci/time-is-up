package guru.bonacci.timesup.track.produce;

import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.ValidationException;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import guru.bonacci.timesup.track.model.Track;
import io.confluent.kafka.serializers.KafkaJsonSerializer;
import io.vertx.core.Vertx;

@ApplicationScoped
public class TrackProducer {

	private final Logger log = Logger.getLogger(TrackProducer.class);
	
	@ConfigProperty(name = "kafka.topic", defaultValue = "track") 
	String topic;

	@Inject Vertx vertx;
	
	
	Producer<String, Track> producer;

	public TrackProducer(@ConfigProperty(name = "kafka.brokerlist") String brokers) {
		producer = new KafkaProducer<>(configure(brokers));
	}

	private Properties configure(final String brokers) {
		var props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaJsonSerializer.class.getName());
		props.put(ProducerConfig.ACKS_CONFIG, "all");
		props.put(ProducerConfig.CLIENT_ID_CONFIG, "the-track-gate");
		return props;
	}

	public void send(@Valid final Track record) {
		if (record != null)
			send(record.TRACKING_NUMBER, record);
		else {
			log.warn("Suspicious incoming request");
			throw new ValidationException("Empty request, why even try?");
		}
	}
	
	public void tombstone(final String key, final long delay) {
		vertx.setTimer(delay, id -> {
			send(key, null);
		});
	}

	private void send(final String key, final Track value) {
		log.infof("Producing record: %s\t%s", key, value);

		producer.send(new ProducerRecord<String, Track>(topic, key, value), new Callback() {
			@Override
			public void onCompletion(RecordMetadata m, Exception e) {
				if (e != null) {
					e.printStackTrace();
				} else {
					log.infof("Produced record to topic %s partition [%d] @ offset %d", m.topic(),
							m.partition(), m.offset());
				}
			}
		});

		log.infof("message sent to topic %s", topic);
	}
}
