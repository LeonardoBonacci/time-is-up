package guru.bonacci.timesup.home.streams;

import guru.bonacci.timesup.home.model.TraceAggr;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


@ToString
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class TraceData {

	public String trackingNumber;
    @Getter public long msUntilArrival;
    
    public static TraceData from(TraceAggr trace) {
        return new TraceData(trace.trackingNumber, trace.msUntilArrival);
    }
}