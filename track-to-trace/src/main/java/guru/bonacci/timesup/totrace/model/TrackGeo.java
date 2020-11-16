package guru.bonacci.timesup.totrace.model;

import javax.json.bind.annotation.JsonbProperty;

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
public class TrackGeo {

	@JsonbProperty("mover_id") public String moverId;
	@JsonbProperty("tracking_number") public String trackingNumber;
	@JsonbProperty("unmoved_id") public String unmovedId;
	@JsonbProperty("unmoved_geohash") public String unmovedGeohash;
	@JsonbProperty("unmoved_lat") public double unmovedLat;
	@JsonbProperty("unmoved_lon") public double unmovedLon;
}