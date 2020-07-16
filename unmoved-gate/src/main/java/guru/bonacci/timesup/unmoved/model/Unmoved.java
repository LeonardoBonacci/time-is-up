package guru.bonacci.timesup.unmoved.model;

import javax.validation.constraints.NotBlank;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.ToString;

@ToString
@RegisterForReflection 
public class Unmoved {

	@NotBlank(message="Where's the id?")
	public String id;

	public Double lat;
    public Double lon;
}