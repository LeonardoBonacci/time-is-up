package guru.bonacci.timesup.trackgeo.joiners;

import org.apache.kafka.streams.kstream.ValueJoiner;

import guru.bonacci.timesup.trackgeo.model.Mover;
import guru.bonacci.timesup.trackgeo.model.Trace;
import guru.bonacci.timesup.trackgeo.model.TrackGeo;

public class MoverTrackGeoJoiner implements ValueJoiner<Mover, TrackGeo, Trace> {

	public Trace apply(Mover mover, TrackGeo trackGeo) {
		return Trace.builder()
				.moverId(trackGeo.moverId)
				.trackingNumber(trackGeo.trackingNumber)
				.unmovedId("..")
				.unmovedGeohash("..")
				.unmovedLat(trackGeo.unmovedLat)
				.unmovedLon(trackGeo.unmovedLon)
				.build();
	}
}
