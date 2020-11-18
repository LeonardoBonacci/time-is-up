package guru.bonacci.timesup.home.model;

import javax.json.bind.annotation.JsonbProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection 
public class Homeward {

	@JsonbProperty("unmoved_id") public String unmovedId;
	@JsonbProperty("tracking_number") public String trackingNumber;
	@JsonbProperty("togo_ms") public long togoMs;
}