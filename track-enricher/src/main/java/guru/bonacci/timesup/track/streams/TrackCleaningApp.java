package guru.bonacci.timesup.track.streams;

import java.time.Duration;
import java.util.Properties;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;

import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TrackCleaningApp {

  static final String TICKER_TOPIC = "ticker";
  static final String COUNTDOWN_TOPIC = "countdown";
  static final String TRACK_TOPIC = "track";
  
  public static void main(final String[] args) {
    final String bootstrapServers = args.length > 0 ? args[0] : "localhost:9092";
    final String schemaRegistryUrl = args.length > 1 ? args[1] : "http://localhost:8081";
//TODO    final boolean doReset = args.length > 1 && args[1].equals("--reset");
    final boolean doReset = true;
    
    final KafkaStreams streams =
        createStreams(bootstrapServers, schemaRegistryUrl);

    if (doReset) { 	   
    	streams.cleanUp();
    }	

    streams.start();
    Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
  }

  public static KafkaStreams createStreams(final String bootstrapServers,
                                           final String schemaRegistryUrl) {

    final Properties config = new Properties();
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "track-cleaning-app");
    config.put(StreamsConfig.CLIENT_ID_CONFIG, "track-cleaning-client");
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
    config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, GenericAvroSerde.class);
    config.put("schema.registry.url", schemaRegistryUrl);
    
    
    final StreamsBuilder builder = new StreamsBuilder();

    final KStream<String, GenericRecord> tickerStream = builder.stream(TICKER_TOPIC);
    final KStream<String, GenericRecord> countdownStream = builder.stream(COUNTDOWN_TOPIC);
    countdownStream.peek((k,v) -> System.out.println("" + v));
    
	tickerStream.join(countdownStream,
					 (t, c) -> c.get("TRACKING_NUMBER"),
					 (JoinWindows.of(Duration.ofSeconds(1))))
				.peek((k,v) -> System.out.println("cleaning track " + v))
				.map((k, v) -> new KeyValue<>(v.toString(), null)) // tombstone
				.peek((k,v) -> System.out.println("cleaning track " + k))
				.to(TRACK_TOPIC);
    
    return new KafkaStreams(builder.build(), config);
  }
}