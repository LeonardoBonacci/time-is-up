package guru.bonacci.timesup.track.model;

import javax.json.bind.annotation.JsonbProperty;
import javax.validation.constraints.NotBlank;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.ToString;

@ToString
@RegisterForReflection 
public class Track {

	@NotBlank(message = "*t*a*k*n*_*u*b*r*")
	@JsonbProperty("tracking_number")
    public String TRACKING_NUMBER;

	@NotBlank(message = "*m*v*r*id*")
	@JsonbProperty("mover_id")
	public String MOVER_ID;

	@NotBlank(message = "*u*m*v*d*id*")
	@JsonbProperty("unmoved_id")
	public String UNMOVED_ID;
}