package guru.bonacci.ksql.udf;

import io.confluent.ksql.function.KsqlFunctionException;
import io.confluent.ksql.function.udf.Udf;
import io.confluent.ksql.function.udf.UdfDescription;
import io.confluent.ksql.function.udf.UdfParameter;
import io.confluent.ksql.util.KsqlConstants;

/**
 * Compute the geohash of a point on the surface of the earth. The input
 * point should be specified as a (lat, lon) pair, measured in decimal degrees.
 *
 * <p>
 * The third parameter allows to specify the hash length. 
 */
@UdfDescription(name = "geohash", description = "Compute the geohash of a point on the surface of the earth.")
public class GeoHash {

	@Udf(description = "The input point should be specified as a (lat, lon) pair, measured in decimal degrees, with a length 1-12.")
	public String geoHash(
			@UdfParameter("lat") final Double lat,
			@UdfParameter("lon") final Double lon,
			@UdfParameter("length") final Integer length) {

		try {
			// davidmoten's library takes care of input validation
			return com.github.davidmoten.geo.GeoHash.encodeHash(lat, lon, length);
		} catch(IllegalArgumentException e) {
			throw new KsqlFunctionException("Invalid input", e);
		}
	}

}