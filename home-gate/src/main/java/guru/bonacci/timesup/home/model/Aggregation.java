package guru.bonacci.timesup.home.model;

import java.util.HashMap;
import java.util.Map;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.ToString;

@ToString
@RegisterForReflection
public class Aggregation {

    public String unmovedId;
	public Map<String, HomeData> homewards = new HashMap<>();
	
	public Aggregation updateFrom(Homeward incoming) {
		this.unmovedId = incoming.unmovedId;
		homewards.put(incoming.trackingNumber, new HomeData(incoming.trackingNumber, incoming.togoMs));

		return this;
	}
    
	public Aggregation merge(Aggregation other) {
		this.unmovedId = other.unmovedId;
		homewards.putAll(other.homewards);

		return this;
	}
}