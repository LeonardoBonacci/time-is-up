package guru.bonacci.timesup.pickup.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection 
public class TimedArrival extends Arrival {

	public TimedArrival(Arrival arrival, long rowtime) {
		this.moverId = arrival.moverId;
		this.trackingNumber = arrival.trackingNumber;
		this.unmovedId = arrival.unmovedId;
        this.rowtime = rowtime;
	}
	
	public long rowtime;
}