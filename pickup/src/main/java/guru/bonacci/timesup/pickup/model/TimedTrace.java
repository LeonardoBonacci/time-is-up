package guru.bonacci.timesup.pickup.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection 
public class TimedTrace extends Trace {

	public TimedTrace(Trace trace, long rowtime) {
		this.moverId = trace.moverId;
		this.moverGeohash = trace.moverGeohash;
		this.moverLat = trace.moverLat;
		this.moverLon = trace.moverLon;
		this.trackingNumber = trace.trackingNumber;
		this.unmovedId = trace.unmovedId;
		this.unmovedGeohash = trace.unmovedGeohash; 
		this.unmovedLat = trace.unmovedLat; 
		this.unmovedLon = trace.unmovedLon;
        this.rowtime = rowtime;
	}
	
	public long rowtime;
}