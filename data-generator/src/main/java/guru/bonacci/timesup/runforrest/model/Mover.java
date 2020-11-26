package guru.bonacci.timesup.runforrest.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.LatLngTool.Bearing;
import com.javadocmd.simplelatlng.util.LengthUnit;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection 
public class Mover {

    public String id;
	public double lat;
	public double lon;
	
	public Mover step(int stepSizeMeters) {
		LatLng start = new LatLng(this.lat, this.lon);
		LatLng dest = LatLngTool.travel(start, Bearing.EAST, stepSizeMeters, LengthUnit.METER);
		log.info("mover '{}' travels from {} to {} ", this.id, start, dest);

		this.lat = new BigDecimal(dest.getLatitude()).setScale(5, RoundingMode.HALF_UP).doubleValue();
		this.lon = new BigDecimal(dest.getLongitude()).setScale(5, RoundingMode.HALF_UP).doubleValue();
		return this;
	}
}