package guru.bonacci.timesup.track.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.ToString;

@ToString
@RegisterForReflection 
public class Track {

    public String tracking_number;
    public String mover_id;
    public String unmoved_id;
}