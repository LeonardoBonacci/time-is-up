package guru.bonacci.timesup.cleanse.streams;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import guru.bonacci.timesup.cleanse.rest.TrackEndpoint;


@ApplicationScoped
public class TopologySupplier {

	private final Logger log = Logger.getLogger(TopologySupplier.class);

	@ConfigProperty(name = "kafka.topic", defaultValue = "pickup") 
	String topic;

	@ConfigProperty(name = "cleanse.delay", defaultValue = "60000") 
	int delay;

	@Inject
	@RestClient
	TrackEndpoint track;
	
	@Inject
	ObjectMapper objectMapper;


	@Produces
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();
        
        builder.stream(topic,
                Consumed.with(Serdes.String(), Serdes.String())
            )
        	.peek((k,v) -> log.infof("%s<pickup>%s", k, v))
        	.mapValues(v -> {
        		try {
        			return objectMapper.readValue(v, Map.class).get("TRACKING_NUMBER");
        		} catch (JsonProcessingException e) {
        			return "Houston...";
        		} 
        	})
        	.foreach((unused, trackingNumber) -> track.tombstone((String)trackingNumber, delay));
        
        return builder.build();
    }
}