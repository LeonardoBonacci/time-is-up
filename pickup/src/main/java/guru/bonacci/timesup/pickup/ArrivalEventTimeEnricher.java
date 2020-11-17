package guru.bonacci.timesup.pickup;

import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.processor.ProcessorContext;

import guru.bonacci.timesup.pickup.model.Arrival;
import guru.bonacci.timesup.pickup.model.TimedArrival;

public class ArrivalEventTimeEnricher implements ValueTransformer<Arrival, TimedArrival> {

	private ProcessorContext context;


	@Override
	public void init(ProcessorContext context) {
		this.context = context;
	}

	@Override
	public TimedArrival transform(Arrival value) {
		return new TimedArrival(value, context.timestamp());
	}
	
	@Override
	public void close() {}
}
