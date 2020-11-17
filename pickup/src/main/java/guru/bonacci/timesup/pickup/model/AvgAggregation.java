package guru.bonacci.timesup.pickup.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.json.bind.annotation.JsonbTransient;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.ToString;

@ToString
@RegisterForReflection
public class AvgAggregation {

    @JsonbTransient public int count;
    @JsonbTransient public long sum;
    public long avg;

    public AvgAggregation updateFrom(TraceArrival trace) {
        count++;
        sum += trace.togoMs;
        avg = BigDecimal.valueOf(sum / count)
                .setScale(0, RoundingMode.HALF_UP).longValue();

        return this;
    }
}