package guru.bonacci.timesup.home.streams;

import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.toList;

import java.util.List;

import javax.json.bind.annotation.JsonbProperty;

import guru.bonacci.timesup.home.model.Aggregation;
import guru.bonacci.timesup.home.model.HomeData;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;


@ToString
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class HomewardData {

	@JsonbProperty("unmoved_id") public String unmovedId;
    public List<HomeData> expected;
    

    public static HomewardData from(Aggregation aggregation) {
        return new HomewardData(
                aggregation.unmovedId,
                aggregation.homewards.values().stream()
            				.map(HomeData::from)
            				.sorted(comparingLong(HomeData::getTogoMs))
            				.collect(toList()));
    }
}