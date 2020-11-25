package guru.bonacci.timesup.rtrack.model;

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
public class Unmoved {

	public String id;
	public double lat;
	public double lon;
}