package guru.bonacci.timesup.unmoved.model;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Range;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.ToString;

@ToString
@RegisterForReflection 
public class Unmoved {

	@NotBlank(message = "*id*")
	public String id;

	@Range(min = -90, max = 90, message = "Where the lat are you?")
	public double lat;

	@Range(min = -180, max = 80, message = "Where the lon are you?")
	public double lon;
}