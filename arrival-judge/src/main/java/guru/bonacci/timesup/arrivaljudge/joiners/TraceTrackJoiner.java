package guru.bonacci.timesup.arrivaljudge.joiners;

import org.apache.kafka.streams.kstream.ValueJoiner;
import org.apache.lucene.util.SloppyMath;

import com.github.davidmoten.geo.GeoHash;

import guru.bonacci.timesup.arrivaljudge.model.TraceIn;
import guru.bonacci.timesup.arrivaljudge.model.TraceOut;
import guru.bonacci.timesup.arrivaljudge.model.Track;

public class TraceTrackJoiner implements ValueJoiner<TraceIn, Track, TraceOut> {

	public TraceOut apply(TraceIn trace, Track track) {
		final var togoKms = SloppyMath.haversinMeters(trace.moverLat, trace.moverLon, trace.unmovedLat, trace.unmovedLon) / 1000;
		return TraceOut.builder()
				.moverId(trace.moverId)
				.moverGeohash(geoHashLength(trace.moverLat, trace.moverLon, togoKms))
				.moverLat(trace.moverLat)
				.moverLon(trace.moverLon)
				.trackingNumber(trace.trackingNumber)
				.unmovedId(trace.unmovedId)
				.unmovedGeohash(geoHashLength(trace.moverLat, trace.moverLon, 8)) //always 8?
				.unmovedLat(trace.unmovedLat)
				.unmovedLon(trace.unmovedLon)
				.togoKms(togoKms)
				.arrivalRadiusMeters(track.arrivalRadiusMeters)
				.build();
	}
	
    private String geoHashLength(double moverLat, double moverLon, double distanceInKilometers) {
		int geohashLength = 4; // >= 100 km
    	if (distanceInKilometers < 1.0) {
    		geohashLength = 8;
    	} else if (distanceInKilometers < 10.0) {
    		geohashLength = 7;
    	} else if (distanceInKilometers < 50.0) {
    		geohashLength = 6;
    	} else if (distanceInKilometers < 100.0) {
    		geohashLength = 5;
    	}	
		return GeoHash.encodeHash(moverLat, moverLon, geohashLength);
    }
}
