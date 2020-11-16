package guru.bonacci.timesup.totrace.joiners;

import org.apache.kafka.streams.kstream.ValueJoiner;

import guru.bonacci.timesup.totrace.model.Mover;
import guru.bonacci.timesup.totrace.model.Trace;
import guru.bonacci.timesup.totrace.model.TrackGeo;

public class MoverTrackGeoJoiner implements ValueJoiner<Mover, TrackGeo, Trace> {

	public Trace apply(Mover mover, TrackGeo trackGeo) {
		return Trace.builder()
				.moverId(trackGeo.moverId)
				.trackingNumber(trackGeo.trackingNumber)
				.unmovedId(trackGeo.unmovedId)
				.unmovedGeohash("..")
				.unmovedLat(trackGeo.unmovedLat)
				.unmovedLon(trackGeo.unmovedLon)
				.build();
	}
}
