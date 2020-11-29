package guru.bonacci.timesup.tracktrace.joiners;

import org.apache.kafka.streams.kstream.ValueJoiner;

import guru.bonacci.timesup.tracktrace.model.Mover;
import guru.bonacci.timesup.tracktrace.model.Trace;
import guru.bonacci.timesup.tracktrace.model.Track;

public class MoverTrackJoiner implements ValueJoiner<Mover, Track, Trace> {

	public Trace apply(Mover mover, Track track) {
		return Trace.builder()
				.moverId(track.moverId)
				.moverLat(mover.lat)
				.moverLon(mover.lon)
				.trackingNumber(track.trackingNumber)
				.unmovedId(track.unmovedId)
				.unmovedLat(track.unmovedLat)
				.unmovedLon(track.unmovedLon)
				.build();
	}
}
