package guru.bonacci.timesup.arrivaljudge.streams;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.Produced;

import guru.bonacci.timesup.arrivaljudge.joiners.TraceTrackJoiner;
import guru.bonacci.timesup.arrivaljudge.model.Arrival;
import guru.bonacci.timesup.arrivaljudge.model.TraceIn;
import guru.bonacci.timesup.arrivaljudge.model.TraceOut;
import guru.bonacci.timesup.arrivaljudge.model.Track;
import io.quarkus.kafka.client.serialization.JsonbSerde;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class ArrivalJudgeTopology {

    static final String TRACK_TOPIC = "track";
    static final String TRACE_UNFILTERED_TOPIC = "trace-unfiltered";
    static final String TRACE_TOPIC = "trace";
    static final String ARRIVAL_TOPIC = "arrival";

    
    @Produces
    public Topology buildTopology() {
    	final var builder = new StreamsBuilder();

    	final GlobalKTable<String, Track> trackTable = builder.globalTable(
            TRACK_TOPIC,
            Consumed.with(Serdes.String(), new JsonbSerde<>(Track.class)));

    	// join a stream of traces with a table of tracks to filter out traces of deleted tracks
		builder.stream(                                                       
            TRACE_UNFILTERED_TOPIC,
            Consumed.with(Serdes.String(), new JsonbSerde<>(TraceIn.class))
        )
        .peek(
    		(k,v) -> log.info("Incoming... {}:{}", k, v)
        )
        .join(                                                        
    		trackTable,
            (traceId, trace) -> trace.trackingNumber,
            new TraceTrackJoiner()
        )
        .peek(
    		(k,v) -> log.info("Outgoing... {}:{}", k, v)
        )
        .through(
    		TRACE_TOPIC,
    		Produced.with(Serdes.String(), new JsonbSerde<>(TraceOut.class))
        )
        .peek(
    		(k,v) -> log.info("meters to go {} radius {}", v.togoKms * 1000, v.arrivalRadiusMeters)
    	)	
        .filter(
        	(k,v) -> v.togoKms * 1000 < v.arrivalRadiusMeters
        )
        .mapValues(
        	(k,v) -> new Arrival(v.moverId, v.trackingNumber, v.unmovedId) 
        )
        .peek(
    		(k,v) -> log.info("Arrival... {}:{}", k, v)
    	)	
        .to(
        	ARRIVAL_TOPIC,
    		Produced.with(Serdes.String(), new JsonbSerde<>(Arrival.class))
        );
        
        return builder.build();
    }
}