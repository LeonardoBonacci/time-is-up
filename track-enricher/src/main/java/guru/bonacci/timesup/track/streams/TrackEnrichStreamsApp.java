package guru.bonacci.timesup.track.streams;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;

import guru.bonacci.timesup.model.UnmovedGeo;
import io.confluent.kafka.streams.serdes.protobuf.KafkaProtobufSerde;
import lombok.RequiredArgsConstructor;

public class TrackEnrichStreamsApp {

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
        createStreams(bootstrapServers, schemaRegistryUrl, "/tmp/unmoved-global-table");

    if (doReset) { 	   
    	streams.cleanUp();
    }	

    streams.start();
    Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
  }

  public static KafkaStreams createStreams(final String bootstrapServers,
                                           final String schemaRegistryUrl,
                                           final String stateDir) {

    final Properties streamsConfiguration = new Properties();
    streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "track-enrichment-app");
    streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, "track-enrichment-client");
    streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    streamsConfiguration.put(StreamsConfig.STATE_DIR_CONFIG, stateDir);
    streamsConfiguration.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

    final KafkaProtobufSerde<guru.bonacci.timesup.model.Track.ConnectDefault1> trackSerde = 
    		new KafkaProtobufSerde<>(guru.bonacci.timesup.model.Track.ConnectDefault1.class);
    final Map<String, String> serdeConfig = Collections.singletonMap("schema.registry.url", schemaRegistryUrl);
    trackSerde.configure(serdeConfig, false);

    final KafkaProtobufSerde<guru.bonacci.timesup.model.UnmovedGeo.ConnectDefault1> unmovedSerde = 
    		new KafkaProtobufSerde<>(guru.bonacci.timesup.model.UnmovedGeo.ConnectDefault1.class);
    unmovedSerde.configure(serdeConfig, false);

    final KafkaProtobufSerde<guru.bonacci.timesup.model.TrackGeo.ConnectDefault1> enrichedTrackSerde = 
    		new KafkaProtobufSerde<>(guru.bonacci.timesup.model.TrackGeo.ConnectDefault1.class);
    enrichedTrackSerde.configure(serdeConfig, false);

    final StreamsBuilder builder = new StreamsBuilder();

    final KStream<String, guru.bonacci.timesup.model.Track.ConnectDefault1> trackStream = 
    		builder.stream(TRACK_TOPIC, Consumed.with(Serdes.String(), trackSerde));

    final GlobalKTable<String, guru.bonacci.timesup.model.UnmovedGeo.ConnectDefault1>
        unmovedTable =
        builder.globalTable(UNMOVED_TOPIC, Materialized.<String, UnmovedGeo.ConnectDefault1, KeyValueStore<Bytes, byte[]>>as(UNMOVED_STORE)
                .withKeySerde(Serdes.String())
                .withValueSerde(unmovedSerde));

    final KStream<String, guru.bonacci.timesup.model.TrackGeo.ConnectDefault1> enrichedTrackStream = 
    		trackStream.join(unmovedTable,
    						(trackNumber, track) -> track.getUNMOVEDID(),
	    					 (track, unmoved) -> new JoinHelper(track, unmoved).build());
    
    enrichedTrackStream
    	.selectKey((trackingNumber, trackGeo) -> trackGeo.getMOVERID()) // prepare to join on mover_id
    	.to(ENRICHED_TRACK_TOPIC, Produced.with(Serdes.String(), enrichedTrackSerde));

    return new KafkaStreams(builder.build(), streamsConfiguration);
  }

  
  @RequiredArgsConstructor
  private static class JoinHelper {

	 private final guru.bonacci.timesup.model.Track.ConnectDefault1 track;
	 private final guru.bonacci.timesup.model.UnmovedGeo.ConnectDefault1 unmoved;

	 guru.bonacci.timesup.model.TrackGeo.ConnectDefault1 build() {
		 return guru.bonacci.timesup.model.TrackGeo.ConnectDefault1.newBuilder()
			.setTRACKINGNUMBER(track.getTRACKINGNUMBER())
			.setMOVERID(track.getMOVERID())
			.setUNMOVEDID(track.getUNMOVEDID())
			.setUNMOVEDGEOHASH(unmoved.getGEOHASH()).build();
	 }
  }
}