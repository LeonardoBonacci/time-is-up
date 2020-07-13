package guru.bonacci.timesup.home.model;

import java.util.Map;

import com.google.common.collect.Maps;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.ToString;

@ToString
@RegisterForReflection
public class UnmovedAggr {

	public String unmovedId;
	public Map<String, TraceAggr> traces = Maps.newHashMap();
	
	
	public UnmovedAggr updateFrom(Trace incoming) {
		this.unmovedId = incoming.unmoved_id;
		traces.put(incoming.unmoved_id, new TraceAggr(incoming.tracking_number, incoming.togo_ms));

		return this;
	}
	
	public UnmovedAggr merge(UnmovedAggr other) {
		this.unmovedId = other.unmovedId;

		// train arrival values are automatically updated here
//		trains.putAll(other.trains);

		return this;
	}
}