package guru.bonacci.timesup.runmoved.model;

import javax.json.bind.annotation.JsonbProperty;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Range;

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
public class Unmoved {

	@NotBlank(message = "*id*")
	public String id;

	@Range(min = -90, max = 90, message = "Where on earth is your lat?")
	public double lat;

	@Range(min = -180, max = 80, message = "Where on earth is your lon?")
	public double lon;
	
	@Min(value = 20)
	@JsonbProperty("arrival_radius_meters") 
	public int arrivalRadiusMeters;
}