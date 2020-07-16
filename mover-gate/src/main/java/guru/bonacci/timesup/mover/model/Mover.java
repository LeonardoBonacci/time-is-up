package guru.bonacci.timesup.mover.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.ToString;

@ToString
@RegisterForReflection 
public class Mover {

    public String id;
    public String lat;
    public String lon;
}