package guru.bonacci.timesup.pickup.streams;

import java.time.Duration;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.StreamJoined;

import guru.bonacci.timesup.pickup.joiners.TraceArrivalJoiner;
import guru.bonacci.timesup.pickup.model.Arrival;
import guru.bonacci.timesup.pickup.model.AvgAggregation;
import guru.bonacci.timesup.pickup.model.TimedArrival;
import guru.bonacci.timesup.pickup.model.TimedTrace;
import guru.bonacci.timesup.pickup.model.Trace;
import guru.bonacci.timesup.pickup.model.TraceArrival;
import io.quarkus.kafka.client.serialization.JsonbSerde;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class PickupTopology {

    static final String ARRIVAL_TOPIC = "arrival";
    static final String TRACE_TOPIC = "trace";
    static final String AVERAGER_TOPIC = "geohash-averager";

    
    @Produces
    public Topology buildTopology() {
    	final var builder = new StreamsBuilder();

        final KStream<String, TimedArrival> arrivalStream = builder.stream(                                                       
            ARRIVAL_TOPIC,
            Consumed.with(Serdes.String(), new JsonbSerde<>(Arrival.class))
        )
        .transformValues(
    		() -> new ArrivalEventTimeEnricher() // copies rowtime to payload
        )
        .peek(
    		(k,v) -> log.info("Incoming... {}:{}", k, v)
        );

        // join a stream of traces with a stream of last hour arrivals
        builder.stream(                                                       
            TRACE_TOPIC,
            Consumed.with(Serdes.String(), new JsonbSerde<>(Trace.class))
        )
        .transformValues(
    		() -> new TraceEventTimeEnricher() // copies rowtime to payload
        )
        .peek(
    		(k,v) -> log.info("Incoming... {}:{}", k, v)
        )
        .join(                                                        
    		arrivalStream,
    		new TraceArrivalJoiner(),
            JoinWindows.of(Duration.ofHours(1)).after(Duration.ZERO), // traces < 1 hour after arrivals
            StreamJoined.with(Serdes.String(), new JsonbSerde<>(TimedTrace.class), new JsonbSerde<>(TimedArrival.class)) 
        )
        .peek(
    		(k,v) -> log.info("Outgoing trace... {}:{}", k, v)
        )
        .groupBy(
    		(k,v) -> v.moverGeohash + '/' + v.unmovedGeohash,
    		Grouped.with(Serdes.String(), new JsonbSerde<>(TraceArrival.class))
		)
        .aggregate(                                                   
            AvgAggregation::new,
            (stationId, value, aggregation) -> aggregation.updateFrom(value),
            Materialized.with(Serdes.String(), new JsonbSerde<>(AvgAggregation.class))
        )
        .toStream()
        .peek(
    		(k,v) -> log.info("Averages... {}:{}", k, v)
        )
        .mapValues(
        	AvgAggregation::getAvg)
        .to(
        	AVERAGER_TOPIC, 
        	Produced.with(Serdes.String(), Serdes.Long())
        );
        
        return builder.build();
    }
}