package guru.bonacci.timesup.cleanse.streams;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.metrics.Gauge;
import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetadataBuilder;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricType;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import guru.bonacci.timesup.cleanse.rest.TrackEndpoint;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class CleanserPipeline {

    @Inject MetricRegistry metricRegistry;
    AtomicLong success = new AtomicLong();
    AtomicLong failure = new AtomicLong();
    
	@Inject @RestClient TrackEndpoint track;
	
	private KafkaStreams streams;
	
	@ConfigProperty(name = "quarkus.kafka-streams.topics")
	String topic;

	@ConfigProperty(name = "app.name")
	String appId;

	
    void onStart(@Observes StartupEvent event) {
    	var props = new Properties();
	    props.put(StreamsConfig.APPLICATION_ID_CONFIG, appId);
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

		var builder = new StreamsBuilder();

        builder.stream(                                                       
            topic,
            Consumed.with(Serdes.String(), Serdes.serdeFrom(new JsonSerializer(), new JsonDeserializer()))
        )
	    .peek(
	    	(k,v) -> log.info("Incoming... {}:{}", k, v)
        )
    	.mapValues(v -> {
    		try {
    			String tn = v.get("tracking_number").textValue();
        		success.incrementAndGet();
        		return tn;
    		} catch (RuntimeException e) {
    			log.error(e.getMessage());
        		failure.incrementAndGet();
    			return "Houston...";
    		}}
    	)
	    .peek(
	    	(k,v) -> log.info("Outgoing... {}:{}", k, v)
        )
        .filterNot(
        	(k,v) -> "Houston...".equals(v)
		)		
    	.foreach(
    		(unused, trackingNumber) -> track.tombstone(trackingNumber)
		);

        streams = new KafkaStreams(builder.build(), props);
        streams.start();
        
        exportCustomMetrics();
    }
	
	void onStop(@Observes ShutdownEvent event) {
        streams.close();
    }
	
	private void exportCustomMetrics() {
    	Metadata metadataS = 
    			new MetadataBuilder()
    				.withName("tracks.deleted")
    				.withDescription("nr of tracks deleted records")
    				.withType(MetricType.GAUGE).build();

    	metricRegistry.register(metadataS, (Gauge<Long>)() -> success.get());

    	Metadata metadataF = 
    			new MetadataBuilder()
    				.withName("tracks.failed")
    				.withDescription("nr of tracks failed to be deleted")
    				.withType(MetricType.GAUGE).build();

    	metricRegistry.register(metadataF, (Gauge<Long>)() -> failure.get());
    }
}