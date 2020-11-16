package guru.bonacci.timesup.trackgeo.joiners;

import org.apache.kafka.streams.kstream.ValueJoiner;

import com.github.davidmoten.geo.GeoHash;

import guru.bonacci.timesup.trackgeo.model.Track;
import guru.bonacci.timesup.trackgeo.model.TrackGeo;
import guru.bonacci.timesup.trackgeo.model.Unmoved;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TrackUnmovedJoiner implements ValueJoiner<Track, Unmoved, TrackGeo> {

	private final int hashLength;
	
	public TrackGeo apply(Track track, Unmoved unmoved) {
		return TrackGeo.builder()
				.moverId(track.moverId)
				.trackingNumber(track.trackingNumber)
				.unmovedId(unmoved.id)
				.unmovedGeohash(GeoHash.encodeHash(unmoved.lat, unmoved.lon, hashLength))
				.unmovedLat(unmoved.lat)
				.unmovedLon(unmoved.lon)
				.build();
	}
}
