package guru.bonacci.timesup.trackgeo;

import java.time.Duration;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.Repartitioned;

import guru.bonacci.timesup.trackgeo.joiners.MoverTrackGeoJoiner;
import guru.bonacci.timesup.trackgeo.joiners.TrackUnmovedJoiner;
import guru.bonacci.timesup.trackgeo.model.Mover;
import guru.bonacci.timesup.trackgeo.model.Trace;
import guru.bonacci.timesup.trackgeo.model.Track;
import guru.bonacci.timesup.trackgeo.model.TrackGeo;
import guru.bonacci.timesup.trackgeo.model.Unmoved;
import io.quarkus.kafka.client.serialization.JsonbSerde;

@ApplicationScoped
public class TopologyProducer {

    private static final String UNMOVED_TOPIC = "unmoved";
    private static final String TRACK_TOPIC = "track";
    private static final String MOVER_TOPIC = "mover";
    private static final String TRACE_TOPIC = "trace";

    @Produces
    public Topology buildTopology() {
    	final StreamsBuilder builder = new StreamsBuilder();

    	// a table of unmoved's
    	final GlobalKTable<String, Unmoved> unmovedTable = builder.globalTable( 
                UNMOVED_TOPIC,
                Consumed.with(Serdes.String(), new JsonbSerde<>(Unmoved.class)));

    	// join a stream of tracks with the table of unmoved's in order to add geo-info
        final KStream<String, TrackGeo> trackGeoStream = 
        		builder.stream(                                                       
                        TRACK_TOPIC,
                        Consumed.with(Serdes.String(), new JsonbSerde<>(Track.class))
                )
                .join(                                                        
                		unmovedTable,
                        (trackId, track) -> track.unmovedId,
                        new TrackUnmovedJoiner()
                )
                .selectKey(
                		(trackId, track) -> track.moverId
                )	
                .repartition(                                                          
                        Repartitioned.with(Serdes.String(), new JsonbSerde<>(TrackGeo.class))
                );

        
    	// join a stream of movers with the table of track-geo's to create traces
        builder.stream(                                                       
                MOVER_TOPIC,
                Consumed.with(Serdes.String(), new JsonbSerde<>(Mover.class))
        )
        .join(                                                        
        		trackGeoStream,
        		new MoverTrackGeoJoiner(),
                JoinWindows.of(Duration.ofMillis(20000)) //before/after
        ).to(
        		TRACE_TOPIC,
        		Produced.with(Serdes.String(), new JsonbSerde<>(Trace.class))
        );
        
        return builder.build();
    }
}