package guru.bonacci.timesup.trackgeo;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.Produced;

import guru.bonacci.timesup.trackgeo.joiners.TrackUnmovedJoiner;
import guru.bonacci.timesup.trackgeo.model.Track;
import guru.bonacci.timesup.trackgeo.model.TrackGeo;
import guru.bonacci.timesup.trackgeo.model.Unmoved;
import io.quarkus.kafka.client.serialization.JsonbSerde;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class TopologyProducer {

    static final String UNMOVED_TOPIC = "unmoved";
    static final String TRACK_TOPIC = "track";
    static final String TRACK_GEO_TOPIC = "track-geo";

    
    @Produces
    public Topology buildTopology() {
    	final StreamsBuilder builder = new StreamsBuilder();

    	// a table of unmoved's
    	final GlobalKTable<String, Unmoved> unmovedTable = builder.globalTable( 
                UNMOVED_TOPIC,
                Consumed.with(Serdes.String(), new JsonbSerde<>(Unmoved.class)));

    	// join a stream of tracks with a table of unmoved's in order to add geo-info
		builder.stream(                                                       
                TRACK_TOPIC,
                Consumed.with(Serdes.String(), new JsonbSerde<>(Track.class))
        )
        .peek(
        		(k,v) -> log.info("Incoming track... {}:{}", k, v)
        )
        .join(                                                        
        		unmovedTable,
                (trackId, track) -> track.unmovedId,
                new TrackUnmovedJoiner()
        )
        .selectKey(
        		(trackId, track) -> track.moverId
        )
        .peek(
        		(k,v) -> log.info("Outgoing track... {}:{}", k, v)
        )
        .to(
        		TRACK_GEO_TOPIC, //TODO compacted topic                                                      
                Produced.with(Serdes.String(), new JsonbSerde<>(TrackGeo.class))
        );
        
        return builder.build();
    }
}