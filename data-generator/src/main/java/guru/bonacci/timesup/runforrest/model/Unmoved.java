package guru.bonacci.timesup.runforrest.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection 
public class Unmoved {

	public String id;
	public double lat;
	public double lon;
}