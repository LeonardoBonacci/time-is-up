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
		this.unmovedId = incoming.UNMOVED_ID;
		traces.put(incoming.TRACKING_NUMBER, new TraceAggr(incoming.TRACKING_NUMBER, incoming.TOGO_MS));

		return this;
	}
	
	public UnmovedAggr merge(UnmovedAggr other) {
		this.unmovedId = other.unmovedId;
		traces.putAll(other.traces);

		return this;
	}
}