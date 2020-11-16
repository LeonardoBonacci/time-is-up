package guru.bonacci.timesup.tracktotrace.joiners;

import org.apache.kafka.streams.kstream.ValueJoiner;

import guru.bonacci.timesup.tracktotrace.model.Track;
import guru.bonacci.timesup.tracktotrace.model.TrackGeo;
import guru.bonacci.timesup.tracktotrace.model.Unmoved;

public class TrackUnmovedJoiner implements ValueJoiner<Track, Unmoved, TrackGeo> {

	public TrackGeo apply(Track track, Unmoved unmoved) {
		return TrackGeo.builder()
				.moverId(track.moverId)
				.trackingNumber(track.trackingNumber)
				.unmovedId(unmoved.id)
				.unmovedGeohash("..")
				.unmovedLat(unmoved.lat)
				.unmovedLon(unmoved.lon)
				.build();
	}
}
