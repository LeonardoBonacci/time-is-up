package guru.bonacci.timesup.rhome;

import guru.bonacci.timesup.rhome.model.Homeward;
import io.quarkus.kafka.client.serialization.JsonbDeserializer;

public class HomewardDeserializer extends JsonbDeserializer<Homeward> {

	public HomewardDeserializer() {
		super(Homeward.class);
	}

	public HomewardDeserializer(Class<Homeward> type) {
		super(type);
	}
}
