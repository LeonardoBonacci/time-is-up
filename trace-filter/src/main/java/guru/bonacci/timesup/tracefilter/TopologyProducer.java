package guru.bonacci.timesup.tracefilter;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.Produced;

import guru.bonacci.timesup.tracefilter.model.Trace;
import guru.bonacci.timesup.tracefilter.model.Track;
import io.quarkus.kafka.client.serialization.JsonbSerde;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class TopologyProducer {

    private static final String TRACK_TOPIC = "track";
    private static final String TRACE_RAW_TOPIC = "trace_raw";
    private static final String TRACE_TOPIC = "trace";

    
    @Produces
    public Topology buildTopology() {
    	final StreamsBuilder builder = new StreamsBuilder();

    	// a table of tracks
    	final GlobalKTable<String, Track> trackTable = builder.globalTable(
                TRACK_TOPIC,
                Consumed.with(Serdes.String(), new JsonbSerde<>(Track.class)));

    	// join a stream of traces with a table of tracks to filter out...
		builder.stream(                                                       
                TRACE_RAW_TOPIC,
                Consumed.with(Serdes.String(), new JsonbSerde<>(Trace.class))
        )
        .peek(
        		(k,v) -> log.debug("Incoming trace... {}:{}", k, v)
        )
        .join(                                                        
        		trackTable,
                (traceId, trace) -> trace.trackingNumber,
                (trace, track) -> trace
        )
        .to(
        		TRACE_TOPIC,
        		Produced.with(Serdes.String(), new JsonbSerde<>(Trace.class))
        );
        
        return builder.build();
    }
}