package guru.bonacci.timesup.runmoved;

import guru.bonacci.timesup.runmoved.model.Unmoved;
import io.quarkus.kafka.client.serialization.JsonbDeserializer;

public class UnmovedDeserializer extends JsonbDeserializer<Unmoved> {

	public UnmovedDeserializer() {
		super(Unmoved.class);
	}

	public UnmovedDeserializer(Class<Unmoved> type) {
		super(type);
	}
}
