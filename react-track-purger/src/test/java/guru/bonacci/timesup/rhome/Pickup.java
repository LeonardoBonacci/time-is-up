package guru.bonacci.timesup.rhome;

import javax.json.bind.annotation.JsonbProperty;

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
public class Pickup {

	@JsonbProperty("mover_id") public String moverId;
	@JsonbProperty("mover_lat") public double moverLat;
	@JsonbProperty("mover_lon") public double moverLon;
	@JsonbProperty("mover_geohash") public String moverGeohash;
	@JsonbProperty("tracking_number") public String trackingNumber;
	@JsonbProperty("unmoved_id") public String unmovedId;
	@JsonbProperty("unmoved_geohash") public String unmovedGeohash;
	@JsonbProperty("unmoved_lat") public double unmovedLat;
	@JsonbProperty("unmoved_lon") public double unmovedLon;
	@JsonbProperty("togo_ms") public long togoMs;
}