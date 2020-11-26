package guru.bonacci.timesup.rtrack;

import javax.json.bind.annotation.JsonbProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection 
public class TrackResponse {

	@JsonbProperty("tracking_number") public String trackingNumber;
	@JsonbProperty("unmoved_lat") public double unmovedLat;
	@JsonbProperty("unmoved_lon") public double unmovedLon;
}