package guru.bonacci.timesup.trackgeo;

import org.apache.kafka.streams.kstream.ValueJoiner;

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
