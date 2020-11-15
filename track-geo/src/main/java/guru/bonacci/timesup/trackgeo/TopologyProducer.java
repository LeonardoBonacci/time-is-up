package guru.bonacci.timesup.trackgeo;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.Produced;

import io.quarkus.kafka.client.serialization.JsonbSerde;

@ApplicationScoped
public class TopologyProducer {

    private static final String UNMOVED_TOPIC = "unmoved";
    private static final String TRACK_TOPIC = "track";
    private static final String TRACK_GEO_TOPIC = "track-geo";

    @Produces
    public Topology buildTopology() {
    	final StreamsBuilder builder = new StreamsBuilder();

    	final JsonbSerde<Unmoved> unmovedSerde = new JsonbSerde<>(Unmoved.class);
    	final JsonbSerde<Track> trackSerde = new JsonbSerde<>(Track.class);
    	final JsonbSerde<TrackGeo> trackGeoSerde = new JsonbSerde<>(TrackGeo.class);

        final TrackUnmovedJoiner joiner = new TrackUnmovedJoiner();

        final GlobalKTable<String, Unmoved> unmovedTable = builder.globalTable( 
                UNMOVED_TOPIC,
                Consumed.with(Serdes.String(), unmovedSerde));
        
        builder.stream(                                                       
                        TRACK_TOPIC,
                        Consumed.with(Serdes.String(), trackSerde)
                )
                .join(                                                        
                		unmovedTable,
                        (trackId, track) -> track.unmovedId,
                        joiner
                )
                .to(                                                          
                        TRACK_GEO_TOPIC,
                        Produced.with(Serdes.String(), trackGeoSerde)
                );

        return builder.build();
    }
}