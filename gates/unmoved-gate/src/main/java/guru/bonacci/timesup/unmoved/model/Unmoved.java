package guru.bonacci.timesup.unmoved.model;

import javax.json.bind.annotation.JsonbProperty;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Range;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.ToString;

@ToString
@RegisterForReflection 
public class Unmoved {

	@NotBlank(message = "*id*")
	@JsonbProperty("id")
	public String ID;

	@Range(min = -90, max = 90, message = "Where the lat are you?")
	@JsonbProperty("lat")
	public double LAT;

	@Range(min = -180, max = 80, message = "Where the lon are you?")
	@JsonbProperty("lon")
	public double LON;
}