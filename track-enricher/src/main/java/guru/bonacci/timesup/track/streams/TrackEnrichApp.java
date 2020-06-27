package guru.bonacci.timesup.track.streams;

import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;

import guru.bonacci.timesup.model.Track;
import guru.bonacci.timesup.model.TrackGeo;
import guru.bonacci.timesup.model.UnmovedGeo;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TrackEnrichApp {

  static final String TRACK_TOPIC = "track";
  static final String UNMOVED_TOPIC = "unmoved_geo";
  static final String ENRICHED_TRACK_TOPIC = "track_geo";
  static final String UNMOVED_STORE = "unmoved-store";
  
  public static void main(final String[] args) {
    final String bootstrapServers = args.length > 0 ? args[0] : "localhost:9092";
    final String schemaRegistryUrl = args.length > 1 ? args[1] : "http://localhost:8081";
//TODO    final boolean doReset = args.length > 1 && args[1].equals("--reset");
    final boolean doReset = true;
    
    final KafkaStreams streams =
        createStreams(bootstrapServers, schemaRegistryUrl, "C:\\tmp\\unmoved-global-table");

    if (doReset) { 	   
    	streams.cleanUp();
    }	

    streams.start();
    Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
  }

  public static KafkaStreams createStreams(final String bootstrapServers,
                                           final String schemaRegistryUrl,
                                           final String stateDir) {

    final Properties config = new Properties();
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "track-enrichment-app");
    config.put(StreamsConfig.CLIENT_ID_CONFIG, "track-enrichment-client");
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    config.put(StreamsConfig.STATE_DIR_CONFIG, stateDir);
    config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
    config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
    config.put("schema.registry.url", schemaRegistryUrl);
    
    
    final StreamsBuilder builder = new StreamsBuilder();

    final KStream<String, Track> trackStream = builder.stream(TRACK_TOPIC);
    trackStream.peek((k,v) -> log.info(k + " <> " + v));

    final GlobalKTable<String, UnmovedGeo> unmovedTable = 
    		builder.globalTable(UNMOVED_TOPIC, Materialized.as(UNMOVED_STORE));

    final KStream<String, TrackGeo> enrichedTrackStream = 
    		trackStream.join(unmovedTable,
    						(trackNumber, track) -> track.getUnmovedId(),
	    					 (track, unmoved) -> new JoinHelper(track, unmoved).build());
    
    enrichedTrackStream
    	.selectKey((trackingNumber, trackGeo) -> trackGeo.getMoverId())
    	.to(ENRICHED_TRACK_TOPIC);

    return new KafkaStreams(builder.build(), config);
  }

  
  @RequiredArgsConstructor
  private static class JoinHelper {

	 private final Track track;
	 private final UnmovedGeo unmoved;

	 TrackGeo build() {
		 return TrackGeo.newBuilder()
			.setTrackingNumber(track.getTrackingNumber())
			.setMoverId(track.getMoverId())
			.setUnmovedId(track.getUnmovedId())
			.setUnmovedGeohash(unmoved.getGeohash())
			.setUnmovedLat(unmoved.getLat())
			.setUnmovedLon(unmoved.getLon())
			.build();
	 }
  }
}