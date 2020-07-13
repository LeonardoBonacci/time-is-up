package guru.bonacci.timesup.home.streams;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import guru.bonacci.timesup.home.model.UnmovedAggr;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;


@ToString
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class UnmovedData {

    public String unmovedId;
    public List<TraceData> expected;
    

    public static UnmovedData from(UnmovedAggr aggregation) {
        return new UnmovedData(
                aggregation.unmovedId,
                aggregation.traces.values().stream()
            				.map(TraceData::from)
            				.sorted(Comparator.comparingLong(TraceData::getMsUntilArrival))
            				.collect(Collectors.toList()));
    }
}