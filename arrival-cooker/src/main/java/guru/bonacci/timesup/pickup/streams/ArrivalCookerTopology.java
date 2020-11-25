package guru.bonacci.timesup.pickup.streams;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;

import guru.bonacci.timesup.pickup.model.Arrival;
import io.quarkus.kafka.client.serialization.JsonbSerde;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class ArrivalCookerTopology {

    static final String ARRIVAL_RAW_TOPIC = "arrival-raw";
    static final String ARRIVAL_TOPIC = "arrival";

    
    @Produces
    public Topology buildTopology() {
    	final var builder = new StreamsBuilder();

        builder.stream(                                                       
            ARRIVAL_RAW_TOPIC,
            Consumed.with(Serdes.String(), Serdes.serdeFrom(new JsonSerializer(), new JsonDeserializer()))
        )
	    .peek(
	    		(k,v) -> log.info("Incoming... {}:{}", k, v)
        )
        .filter(
        	(k,v) -> v.get(ArrivalRawKeyValueMapper.NEARBY) != null // filters out the faraway messages
    	)
        .map(
    		new ArrivalRawKeyValueMapper()
    	)
	    .peek(
	    		(k,v) -> log.info("Outgoing... {}:{}", k, v)
        )
        .to(
        	ARRIVAL_TOPIC, 
        	Produced.with(Serdes.String(), new JsonbSerde<>(Arrival.class))
        );
        
        return builder.build();
    }
}