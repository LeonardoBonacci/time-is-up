package guru.bonacci.timesup.rtrack.model;

import javax.json.bind.annotation.JsonbProperty;
import javax.validation.constraints.NotBlank;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.ToString;

@ToString
@RegisterForReflection 
public class Track {

	@NotBlank(message = "*t*a*k*n*_*u*b*r*")
	@JsonbProperty("tracking_number")
    public String trackingNumber;

	@NotBlank(message = "*m*v*r*id*")
	@JsonbProperty("mover_id")
	public String moverId;

	@NotBlank(message = "*u*m*v*d*id*")
	@JsonbProperty("unmoved_id")
	public String unmovedId;
}