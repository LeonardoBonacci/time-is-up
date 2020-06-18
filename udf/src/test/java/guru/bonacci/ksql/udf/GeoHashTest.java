package guru.bonacci.ksql.udf; 

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import io.confluent.ksql.function.KsqlFunctionException;
import org.junit.Test;

import guru.bonacci.ksql.udf.GeoHash;

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