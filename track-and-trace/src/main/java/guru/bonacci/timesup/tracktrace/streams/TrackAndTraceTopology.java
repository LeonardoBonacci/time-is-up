package guru.bonacci.timesup.tracktrace.streams;

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

import guru.bonacci.timesup.tracktrace.joiners.MoverTrackGeoJoiner;
import guru.bonacci.timesup.tracktrace.model.Mover;
import guru.bonacci.timesup.tracktrace.model.Trace;
import guru.bonacci.timesup.tracktrace.model.Track;
import io.quarkus.kafka.client.serialization.JsonbSerde;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class TrackAndTraceTopology {

    static final String TRACK_TOPIC = "track";
    static final String MOVER_TOPIC = "mover";
    static final String TRACE_UNFILTERED_TOPIC = "trace-unfiltered";

    
    @Produces
    public Topology buildTopology() {
    	final var builder = new StreamsBuilder();

        final KStream<String, Track> trackStream = builder.stream(                                                       
            TRACK_TOPIC,
            Consumed.with(Serdes.String(), new JsonbSerde<>(Track.class))
        )
	    .filterNot( // filter out tombstone messages
    		(k,v) -> v == null
		)
        .selectKey(
    		(k,v) -> v.moverId, Named.as("track-geo-by-moverid")
		)
        .peek(
    		(k,v) -> log.info("Incoming... {}:{}", k, v)
        );

        // join a stream of movers with a stream of track-geo's to create traces
        builder.stream(                                                       
            MOVER_TOPIC,
            Consumed.with(Serdes.String(), new JsonbSerde<>(Mover.class))
        )
        .peek(
    		(k,v) -> log.info("Incoming... {}:{}", k, v)
        )
        .join(                                                        
    		trackStream,
    		new MoverTrackGeoJoiner(), // determines moverGeohash
            JoinWindows.of(Duration.ofDays(1)).after(Duration.ZERO), // tracked for 1 day only
            StreamJoined.with(Serdes.String(), new JsonbSerde<>(Mover.class), new JsonbSerde<>(Track.class)) 
        )
        .peek(
    		(k,v) -> log.info("Outgoing... {}:{}", k, v)
        )
        .to(
    		TRACE_UNFILTERED_TOPIC,
    		Produced.with(Serdes.String(), new JsonbSerde<>(Trace.class))
        );
        
        return builder.build();
    }
}