package guru.bonacci.timesup.unmoved.produce;

import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.Valid;
import javax.validation.ValidationException;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.jboss.logging.Logger;

import guru.bonacci.timesup.unmoved.model.Unmoved;
import io.confluent.kafka.serializers.KafkaJsonSerializer;

@ApplicationScoped
public class UnmovedProducer {

	private final Logger log = Logger.getLogger(UnmovedProducer.class);
	
	static final String TOPIC = "unmoved";
	
	Producer<String, Unmoved> producer;

	public UnmovedProducer() {
		producer = new KafkaProducer<>(configure());
	}

	private Properties configure() {
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,broker:29092");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaJsonSerializer.class.getName());
		props.put(ProducerConfig.ACKS_CONFIG, "all");
		props.put(ProducerConfig.CLIENT_ID_CONFIG, "the-unmoved-gate");
		return props;
	}

	public void send(@Valid final Unmoved record) {
		if (record != null)
			send(record.id, record);
		else {
			log.warn("Suspicious incoming request");
			throw new ValidationException("Empty request, why even try?");
		}
	}
	
	public void tombstone(final String key) {
		send(key, null);
	}
	
	private void send(final String key, final Unmoved value) {
		log.infof("Producing record: %s\t%s", key, value);

		producer.send(new ProducerRecord<String, Unmoved>(TOPIC, key, value), new Callback() {
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

		producer.flush();
		log.infof("message sent to topic %s", TOPIC);
	}
}
