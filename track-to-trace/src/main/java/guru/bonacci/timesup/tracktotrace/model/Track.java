package guru.bonacci.timesup.tracktotrace.model;

import javax.json.bind.annotation.JsonbProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.ToString;

@ToString
@RegisterForReflection 
public class Track {

	@JsonbProperty("tracking_number") public String trackingNumber;
	@JsonbProperty("mover_id") public String moverId;
	@JsonbProperty("unmoved_id") public String unmovedId;
}