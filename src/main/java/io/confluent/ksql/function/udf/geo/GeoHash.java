/*
 * Copyright 2020 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */

package io.confluent.ksql.function.udf.geo;

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
@UdfDescription(name = "geohash", 
				author = KsqlConstants.CONFLUENT_AUTHOR, 
				description = "Compute the geohash of a point on the surface of the earth.")
public class GeoHash {

	@Udf(description = "The input point should be specified as a (lat, lon) pair, measured in decimal degrees.")
	public String geoHash(@UdfParameter(description = "The latitude in decimal degrees.") final double lat,
			@UdfParameter(description = "The longitude in decimal degrees.") final double lon,
			@UdfParameter(description = "The units for the return value.") final int length) {

		try {
			// davidmoten's library takes care of input validation
			return com.github.davidmoten.geo.GeoHash.encodeHash(lat, lon, length);
		} catch(IllegalArgumentException e) {
			throw new KsqlFunctionException(
					"Invalid input", e);
		}
	}
}