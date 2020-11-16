package guru.bonacci.timesup.tracktotrace.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.ToString;

@ToString
@RegisterForReflection 
public class Unmoved {

	public String id;
	public double lat;
	public double lon;
}