package guru.bonacci.timesup.trackgeo.model;

import javax.json.bind.annotation.JsonbProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
@RegisterForReflection 
public class TrackGeo {

	@JsonbProperty("mover_id") public String moverId;
	@JsonbProperty("tracking_number") public String trackingNumber;
	@JsonbProperty("unmoved_id") public String unmovedId;
	@JsonbProperty("unmoved_geohash") public String unmovedGeohash;
	@JsonbProperty("unmoved_lat") public double unmovedLat;
	@JsonbProperty("unmoved_lon") public double unmovedLon;
}