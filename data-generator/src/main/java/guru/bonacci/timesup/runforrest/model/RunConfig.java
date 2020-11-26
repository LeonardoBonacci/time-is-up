package guru.bonacci.timesup.runforrest.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@RegisterForReflection 
public class RunConfig {

	public int distanceMeters;
	public int speedKmHr;
}