package guru.bonacci.timesup.cleanse.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class TraceAggr {

    public String trackingNumber;
    public long msUntilArrival;
}