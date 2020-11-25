package guru.bonacci.timesup.pickup.streams;

import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.processor.ProcessorContext;

import guru.bonacci.timesup.pickup.model.TimedTrace;
import guru.bonacci.timesup.pickup.model.Trace;

public class TraceEventTimeEnricher implements ValueTransformer<Trace, TimedTrace> {

	private ProcessorContext context;


	@Override
	public void init(ProcessorContext context) {
		this.context = context;
	}

	@Override
	public TimedTrace transform(Trace value) {
		return new TimedTrace(value, context.timestamp());
	}
	
	@Override
	public void close() {}
}
