/*
 * Copyright 2019 Confluent Inc.
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import io.confluent.ksql.function.KsqlFunctionException;
import org.junit.Test;

public class GeoHashTest {
  private final GeoHash udf = new GeoHash();

  /*
   * Compute hash London Confluent office.
   */
  @Test
  public void shouldComputeDistanceBetweenLocations() {
    assertEquals("9", udf.geoHash(37.4439, -122.1663, 1));
    assertEquals("9q9jh", udf.geoHash(37.4439, -122.1663, 5));
    assertEquals("9q9jh03nwb", udf.geoHash(37.4439, -122.1663, 10));
  }


  /**
   * Valid values for latitude range from -90->90 decimal degrees, and longitude is from -180->180
   */
  @Test
  public void shouldFailOutOfBoundsCoordinates() {
    // When:
    final Exception e = assertThrows(
        KsqlFunctionException.class,
        () -> udf.geoHash(90.1, -122.1663, 1)
    );

    // Then:
    assertThat(e.getMessage(), containsString(
        "Invalid input"));
  }

  @Test
  public void shouldFailInvalidLength() {
    // When:
    final Exception e = assertThrows(
        KsqlFunctionException.class,
        () -> udf.geoHash(37.4439, -122.1663, 13)
    );

    // Then:
    assertThat(e.getMessage(), containsString(
        "Invalid input"));
  }
}