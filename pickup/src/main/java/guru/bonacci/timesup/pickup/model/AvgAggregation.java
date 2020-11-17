package guru.bonacci.timesup.pickup.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.ToString;

@ToString
@RegisterForReflection
public class AvgAggregation {

    public int count;
    public long sum;
    @Getter public long avg;

    public AvgAggregation updateFrom(TraceArrival trace) {
        count++;
        sum += trace.togoMs;
        avg = BigDecimal.valueOf(sum / count)
                .setScale(0, RoundingMode.HALF_UP).longValue();

        return this;
    }
}