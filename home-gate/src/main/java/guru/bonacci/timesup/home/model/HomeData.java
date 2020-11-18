package guru.bonacci.timesup.home.model;

import javax.json.bind.annotation.JsonbProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class HomeData {

	@JsonbProperty("tracking_number") public String trackingNumber;
	@JsonbProperty("togo_ms") @Getter public long togoMs;
	
    public static HomeData from(HomeData homeward) {
        return new HomeData(homeward.trackingNumber, homeward.togoMs);
    }
}