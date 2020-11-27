package guru.bonacci.timesup.tracktrace.joiners;

import org.apache.kafka.streams.kstream.ValueJoiner;
import org.apache.lucene.util.SloppyMath;

import com.github.davidmoten.geo.GeoHash;

import guru.bonacci.timesup.tracktrace.model.Mover;
import guru.bonacci.timesup.tracktrace.model.Trace;
import guru.bonacci.timesup.tracktrace.model.Track;

public class MoverTrackGeoJoiner implements ValueJoiner<Mover, Track, Trace> {

	public Trace apply(Mover mover, Track trackGeo) {
		return Trace.builder()
				.moverId(trackGeo.moverId)
				.moverGeohash(geoHashLength(mover.lat, mover.lon, trackGeo.unmovedLat, trackGeo.unmovedLon))
				.moverLat(mover.lat)
				.moverLon(mover.lon)
				.trackingNumber(trackGeo.trackingNumber)
				.unmovedId(trackGeo.unmovedId)
				.unmovedGeohash(trackGeo.unmovedGeohash)
				.unmovedLat(trackGeo.unmovedLat)
				.unmovedLon(trackGeo.unmovedLon)
				.build();
	}
	
    private String geoHashLength(double moverLat, double moverLon, double unmovedLat, double unmovedLon) {
		double distanceInKilometers = SloppyMath.haversinMeters(moverLat, moverLon, unmovedLat, unmovedLon) / 1000;

		int geohashLength = 4; // > 100 km
    	if (distanceInKilometers < 1.0) {
    		geohashLength = 8;
    	} else if (distanceInKilometers < 10.0) {
    		geohashLength = 7;
    	} else if (distanceInKilometers < 50.0) {
    		geohashLength = 6;
    	} else if (distanceInKilometers < 100.0) {
    		geohashLength = 5;
    	}	

    	return GeoHash.encodeHash(unmovedLat, unmovedLon, geohashLength);
    }

}
