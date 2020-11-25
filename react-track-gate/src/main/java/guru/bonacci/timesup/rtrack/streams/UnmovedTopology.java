package guru.bonacci.timesup.rtrack.streams;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.Stores;

import guru.bonacci.timesup.rtrack.model.Unmoved;
import io.quarkus.kafka.client.serialization.JsonbSerde;

@ApplicationScoped
public class UnmovedTopology {

	public static final String UNMOVED_STORE = "unmoved-store";
    
	static final String UNMOVED_TOPIC = "unmoved";

    
    @Produces
    public Topology buildTopology() {
    	final StreamsBuilder builder = new StreamsBuilder();

		builder.table(UNMOVED_TOPIC,
                Consumed.with(Serdes.String(), new JsonbSerde<>(Unmoved.class)),
                Materialized.as(Stores.persistentKeyValueStore(UNMOVED_STORE))                
        );
       
        return builder.build();
    }
}