package guru.bonacci.timesup.track.streams;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;

import guru.bonacci.kafka.serialization.JacksonSerde;
import guru.bonacci.timesup.track.model.Track;
import guru.bonacci.timesup.track.model.TrackGeo;
import guru.bonacci.timesup.track.model.UnmovedGeo;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
public class TopologyProducer {

	private static final String TRACK_TOPIC = "track";
	private static final String UNMOVED_TOPIC = "unmoved_geo";
	private static final String ENRICHED_TRACK_TOPIC = "track_geo";
	private static final String UNMOVED_STORE = "unmoved-store";

    @Produces
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        final Serde<Track> trackSerde = JacksonSerde.of(Track.class);
        final Serde<UnmovedGeo> unmovedGeoSerde = JacksonSerde.of(UnmovedGeo.class);
        final Serde<TrackGeo> trackGeoSerde = JacksonSerde.of(TrackGeo.class);
        
        final KStream<String, Track> trackStream = 
        		builder.stream(TRACK_TOPIC, Consumed.with(Serdes.String(), trackSerde));
        trackStream.peek((k,v) -> System.out.println(k + " <> " + v));

        final GlobalKTable<String, UnmovedGeo> unmovedTable = 
        		builder.globalTable(UNMOVED_TOPIC, 
        				Consumed.with(Serdes.String(), unmovedGeoSerde), 
        				Materialized.as(UNMOVED_STORE));
    
        final KStream<String, TrackGeo> enrichedTrackStream = 
        		trackStream.join(unmovedTable,
        						(trackNumber, track) -> track.unmoved_id,
    	    					 (track, unmoved) -> new JoinHelper(track, unmoved).build());
        
        enrichedTrackStream
        	.selectKey((trackingNumber, trackGeo) -> trackGeo.mover_id)
            .peek((k,v) -> System.out.println(k + " <> " + v))
        	.to(ENRICHED_TRACK_TOPIC, Produced.with(Serdes.String(), trackGeoSerde));

        return builder.build();
    }
    
    @RequiredArgsConstructor
    private static class JoinHelper {

  	 private final Track track;
  	 private final UnmovedGeo unmoved;

  	 TrackGeo build() {
  		 return TrackGeo.builder()
  			.tracking_number(track.tracking_number)
  			.mover_id(track.mover_id)
  			.unmoved_id(track.unmoved_id)
  			.unmoved_geohash(unmoved.GEOHASH)
  			.unmoved_lat(unmoved.LAT)
  			.unmoved_lon(unmoved.LON)
  			.build();
  	 }
    }
}
