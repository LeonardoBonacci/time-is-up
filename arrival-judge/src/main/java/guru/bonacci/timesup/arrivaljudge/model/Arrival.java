package guru.bonacci.timesup.arrivaljudge.model;

import javax.json.bind.annotation.JsonbProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection 
public class Arrival {

	@JsonbProperty("mover_id") public String moverId;
	@JsonbProperty("tracking_number") public String trackingNumber;
	@JsonbProperty("unmoved_id") public String unmovedId;
}