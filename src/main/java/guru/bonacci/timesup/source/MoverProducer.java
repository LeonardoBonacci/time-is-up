package guru.bonacci.timesup.source;

import java.util.Properties;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import guru.bonacci.timesup.model.TheMover.Mover;

public class MoverProducer {

	static final String TOPIC = "mover";
	
	Producer<String, Mover> producer;

	public static void main(final String[] args) {
		MoverProducer fooProducer = new MoverProducer();
		fooProducer.send();
	}
	
	public MoverProducer() {
		producer = new KafkaProducer<>(configure());
	}

	private Properties configure() {
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
        					"io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer");
		props.put(ProducerConfig.ACKS_CONFIG, "1");
		props.put(ProducerConfig.CLIENT_ID_CONFIG, "sample-mover-producer");
		props.put("schema.registry.url", "http://127.0.0.1:8081");
		return props;
	}

	// Produce sample data
	void send() {
		final int nrSteps = 11;
		for (int i = 0; i < nrSteps; i++) {
			
			String id = "moverId";
			Pair<Float, Float> step = StepSimulator.stepMover(nrSteps, i);
	    	Mover record = Mover.newBuilder().setId(id).setLat(step.getLeft()).setLon(step.getRight()).build();

			System.out.printf("Producing record: %s\t%s", id, record);
			producer.send(new ProducerRecord<String, Mover>(TOPIC, id, record), new Callback() {
				@Override
				public void onCompletion(RecordMetadata m, Exception e) {
					if (e != null) {
						e.printStackTrace();
					} else {
						System.out.printf("Produced 'mover' to topic %s partition [%d] @ offset %d%n", m.topic(),
								m.partition(), m.offset());
					}
				}
			});
		}

		producer.flush();
		producer.close();
	}

}