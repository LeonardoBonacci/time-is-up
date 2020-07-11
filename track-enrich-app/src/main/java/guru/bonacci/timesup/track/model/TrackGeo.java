package guru.bonacci.timesup.track.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
@RegisterForReflection 
public class TrackGeo {

    public String tracking_number;
	public String mover_id;
	public String unmoved_id;
	public String unmoved_geohash;
    public double unmoved_lat;
    public double unmoved_lon;
}