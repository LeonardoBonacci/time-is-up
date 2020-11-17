package guru.bonacci.timesup.pickup;

import java.time.Duration;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.StreamJoined;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;

import guru.bonacci.timesup.pickup.joiners.TraceArrivalJoiner;
import guru.bonacci.timesup.pickup.model.Arrival;
import guru.bonacci.timesup.pickup.model.TimedArrival;
import guru.bonacci.timesup.pickup.model.TimedTrace;
import guru.bonacci.timesup.pickup.model.Trace;
import io.quarkus.kafka.client.serialization.JsonbSerde;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class PickupTopology {

    static final String TRACE_TOPIC = "trace";
    static final String ARRIVAL_TOPIC = "arrival";
    static final String OUT_TOPIC = "out";

    
    @Produces
    public Topology buildTopology() {
    	final StreamsBuilder builder = new StreamsBuilder();

        final KStream<String, TimedArrival> arrivalStream = builder.stream(                                                       
                ARRIVAL_TOPIC,
                Consumed.with(Serdes.String(), new JsonbSerde<>(Arrival.class), new WallclockTimestampExtractor(), null)
        )
        .transformValues(
        		() -> new ArrivalEventTimeEnricher() // extracts rowtime to payload
        )
        .peek(
        		(k,v) -> log.info("Incoming arrival... {}:{}", k, v)
        );

        // join a stream of traces with a stream of last hour arrivals
        builder.stream(                                                       
                TRACE_TOPIC,
                Consumed.with(Serdes.String(), new JsonbSerde<>(Trace.class))
        )
        .transformValues(
        		() -> new TraceEventTimeEnricher() // extracts rowtime to payload
        )
        .peek(
        		(k,v) -> log.info("Incoming trace... {}:{}", k, v)
        )
        .join(                                                        
        		arrivalStream,
        		new TraceArrivalJoiner(),
                JoinWindows.of(Duration.ofHours(1)).after(Duration.ZERO), // traces < 1 hour after arrivals
                StreamJoined.with(Serdes.String(), new JsonbSerde<>(TimedTrace.class), new JsonbSerde<>(TimedArrival.class)) 
        )
        .peek(
        		(k,v) -> log.info("Outgoing trace... {}:{}", k, v)
        );
        
        return builder.build();
    }
}