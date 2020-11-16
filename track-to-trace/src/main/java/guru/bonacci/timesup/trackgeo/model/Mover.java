package guru.bonacci.timesup.trackgeo.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.ToString;

@ToString
@RegisterForReflection 
public class Mover {

    public String id;
	public double lat;
	public double lon;
}