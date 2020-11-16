package guru.bonacci.timesup.trackgeo.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection 
public class Unmoved {

	public String id;
	public double lat;
	public double lon;
}