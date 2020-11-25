package guru.bonacci.timesup.pickup;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KeyValueMapper;

import com.fasterxml.jackson.databind.JsonNode;

import guru.bonacci.timesup.pickup.model.Arrival;

public class ArrivalRawKeyValueMapper implements KeyValueMapper<String, JsonNode, KeyValue<String, Arrival>> {

	static final String ID = "id";
	static final String NEARBY = "nearby";

	@Override
	public KeyValue<String, Arrival> apply(String key, JsonNode value) {
		final String moverId = value.get(ID).textValue();
		final String unmovedId = value.get(NEARBY).get(ID).textValue();
		return new KeyValue<String, Arrival>(moverId, new Arrival(moverId, unmovedId));
	}
}
