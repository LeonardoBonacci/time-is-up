package guru.bonacci.timesup.pickup.joiners;

import org.apache.kafka.streams.kstream.ValueJoiner;

import guru.bonacci.timesup.pickup.model.TimedArrival;
import guru.bonacci.timesup.pickup.model.TimedTrace;
import guru.bonacci.timesup.pickup.model.TraceArrival;

public class TraceArrivalJoiner implements ValueJoiner<TimedTrace, TimedArrival, TraceArrival> {

	public TraceArrival apply(TimedTrace trace, TimedArrival arrival) {
		return TraceArrival.builder()
				.moverId(trace.moverId)
				.moverLat(trace.moverLat)
				.moverLon(trace.moverLon)
				.moverGeohash(trace.moverGeohash)
				.trackingNumber(trace.trackingNumber)
				.unmovedId(trace.unmovedId)
				.unmovedLat(trace.unmovedLat)
				.unmovedLon(trace.unmovedLon)
				.unmovedGeohash(trace.unmovedGeohash)
				.togoMs(arrival.rowtime - trace.rowtime)
				.build();
	}
}
