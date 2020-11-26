package guru.bonacci.timesup.runforrest.model;

import javax.json.bind.annotation.JsonbProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection 
public class Track {

	@JsonbProperty("tracking_number") public String trackingNumber;
	@JsonbProperty("mover_id") public String moverId;
	@JsonbProperty("unmoved_id") public String unmovedId;
}