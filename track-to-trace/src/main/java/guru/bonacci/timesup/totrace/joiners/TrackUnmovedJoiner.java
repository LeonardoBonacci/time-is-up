package guru.bonacci.timesup.totrace.joiners;

import org.apache.kafka.streams.kstream.ValueJoiner;

import guru.bonacci.timesup.totrace.model.Track;
import guru.bonacci.timesup.totrace.model.TrackGeo;
import guru.bonacci.timesup.totrace.model.Unmoved;

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
