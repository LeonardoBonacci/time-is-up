package guru.bonacci.timesup.unmoved.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.ToString;

@ToString
@RegisterForReflection 
public class Unmoved {

    public String id;
    public String lat;
    public String lon;
}