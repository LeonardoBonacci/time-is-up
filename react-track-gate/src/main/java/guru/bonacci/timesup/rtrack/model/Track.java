package guru.bonacci.timesup.rtrack.model;

import javax.json.bind.annotation.JsonbProperty;
import javax.validation.constraints.NotBlank;

import com.github.davidmoten.geo.GeoHash;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.ToString;

@ToString
@RegisterForReflection 
public class Track {

	@NotBlank(message = "*t*a*k*n*_*u*b*r*")
	@JsonbProperty("tracking_number")
    public String trackingNumber;

	@NotBlank(message = "*m*v*r*id*")
	@JsonbProperty("mover_id")
	public String moverId;

	@NotBlank(message = "*u*m*v*d*id*")
	@JsonbProperty("unmoved_id")
	public String unmovedId;
	
	// the following fields are added before sending the track to the broker
	@JsonbProperty("unmoved_geohash") public String unmovedGeohash;
	@JsonbProperty("unmoved_lat") public double unmovedLat;
	@JsonbProperty("unmoved_lon") public double unmovedLon;
	
	
	public Track enrich(Unmoved unmoved, int hashLength) {
		this.unmovedLat = unmoved.lat;
		this.unmovedLon = unmoved.lon;
		this.unmovedGeohash = GeoHash.encodeHash(unmoved.lat, unmoved.lon, hashLength);
		return this;
	}
}