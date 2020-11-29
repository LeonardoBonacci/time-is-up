package guru.bonacci.timesup.rtrack.model;

import javax.json.bind.annotation.JsonbProperty;
import javax.validation.constraints.NotBlank;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.ToString;

@ToString(onlyExplicitlyIncluded = true)
@RegisterForReflection 
public class Track {

	@ToString.Include
	@JsonbProperty("tracking_number")
    public String trackingNumber;

	@ToString.Include
	@NotBlank(message = "*m*v*r*id*")
	@JsonbProperty("mover_id")
	public String moverId;

	@ToString.Include
	@NotBlank(message = "*u*m*v*d*id*")
	@JsonbProperty("unmoved_id")
	public String unmovedId;
	
	// the following fields are added before sending the track to the broker
	@JsonbProperty("unmoved_lat") public double unmovedLat;
	@JsonbProperty("unmoved_lon") public double unmovedLon;
	@JsonbProperty("arrival_radius_meters") public int arrivalRadiusMeters;
	
	
	public Track enrich(Unmoved unmoved) {
		this.unmovedLat = unmoved.lat;
		this.unmovedLon = unmoved.lon;
		this.trackingNumber = unmoved.id + "--" + this.trackingNumber; 
		this.arrivalRadiusMeters = unmoved.arrivalRadiusMeters;
		return this;
	}
}