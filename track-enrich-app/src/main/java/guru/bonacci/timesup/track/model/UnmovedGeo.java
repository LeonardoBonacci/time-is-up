package guru.bonacci.timesup.track.model;

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
public class UnmovedGeo {

	public String GEOHASH;
    public double LAT;
    public double LON;
}