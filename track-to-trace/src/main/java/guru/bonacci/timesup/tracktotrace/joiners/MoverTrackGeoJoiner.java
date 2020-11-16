package guru.bonacci.timesup.tracktotrace.joiners;

import org.apache.kafka.streams.kstream.ValueJoiner;

import guru.bonacci.timesup.tracktotrace.model.Mover;
import guru.bonacci.timesup.tracktotrace.model.Trace;
import guru.bonacci.timesup.tracktotrace.model.TrackGeo;

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
