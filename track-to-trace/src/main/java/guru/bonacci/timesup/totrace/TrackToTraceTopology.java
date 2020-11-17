package guru.bonacci.timesup.totrace;

import java.time.Duration;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.StreamJoined;

import guru.bonacci.timesup.totrace.joiners.MoverTrackGeoJoiner;
import guru.bonacci.timesup.totrace.model.Mover;
import guru.bonacci.timesup.totrace.model.Trace;
import guru.bonacci.timesup.totrace.model.TrackGeo;
import io.quarkus.kafka.client.serialization.JsonbSerde;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class TrackToTraceTopology {

    static final String TRACK_GEO_TOPIC = "track-geo";
    static final String MOVER_TOPIC = "mover";
    static final String TRACE_UNFILTERED_TOPIC = "trace-unfiltered";

    
    @Produces
    public Topology buildTopology() {
    	final StreamsBuilder builder = new StreamsBuilder();

        final KStream<String, TrackGeo> trackGeoStream = builder.stream(                                                       
                TRACK_GEO_TOPIC,
                Consumed.with(Serdes.String(), new JsonbSerde<>(TrackGeo.class))
        )
        .selectKey(
        		(k,v) -> v.moverId, Named.as("track-geo-by-moverid")
		)
        .peek(
        		(k,v) -> log.info("Incoming track geo... {}:{}", k, v)
        );

        // join a stream of movers with a table of track-geo's to create traces
        builder.stream(                                                       
                MOVER_TOPIC,
                Consumed.with(Serdes.String(), new JsonbSerde<>(Mover.class))
        )
        .peek(
        		(k,v) -> log.info("Incoming mover... {}:{}", k, v)
        )
        .join(                                                        
        		trackGeoStream,
        		new MoverTrackGeoJoiner(),
                JoinWindows.of(Duration.ofDays(1)).after(Duration.ZERO), // track is tracked for 1 day
                StreamJoined.with(Serdes.String(), new JsonbSerde<>(Mover.class), new JsonbSerde<>(TrackGeo.class)) 
        )
        .peek(
        		(k,v) -> log.info("Outgoing mover... {}:{}", k, v)
        )
        .to(
        		TRACE_UNFILTERED_TOPIC,
        		Produced.with(Serdes.String(), new JsonbSerde<>(Trace.class))
        );
        
        return builder.build();
    }
}