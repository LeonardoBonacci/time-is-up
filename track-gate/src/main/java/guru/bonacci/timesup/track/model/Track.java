package guru.bonacci.timesup.track.model;

import javax.validation.constraints.NotBlank;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.ToString;

@ToString
@RegisterForReflection 
public class Track {

	@NotBlank(message = "*t*a*k*n*_*u*b*r*")
    public String tracking_number;

	@NotBlank(message = "*m*v*r*id*")
	public String mover_id;

	@NotBlank(message = "*u*m*v*d*id*")
	public String unmoved_id;
}