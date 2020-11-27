package guru.bonacci.timesup.homeward.joiners;

import org.apache.kafka.streams.kstream.ValueJoiner;

import guru.bonacci.timesup.homeward.model.Homeward;
import guru.bonacci.timesup.homeward.model.Trace;

public class TraceAverageJoiner implements ValueJoiner<Trace, Long, Homeward> {

	public Homeward apply(Trace trace, Long average) {
		return Homeward.builder()
				.moverId(trace.moverId)
				.unmovedId(trace.unmovedId)
				.trackingNumber(trace.trackingNumber)
				.togoMs(average != null ? average : -1)
				.build();
	}
}
