package guru.bonacci.timesup.home.streams;

import java.time.Duration;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.state.Stores;
import org.apache.kafka.streams.state.WindowBytesStoreSupplier;

import guru.bonacci.timesup.home.model.Aggregation;
import guru.bonacci.timesup.home.model.Homeward;
import io.quarkus.kafka.client.serialization.JsonbSerde;

@ApplicationScoped
public class HomeTopology {

	static final String HOME_STORE = "home-store";
    
	static final String HOMEWARD_TOPIC = "homeward";

    
    @Produces
    public Topology buildTopology() {
    	final var builder = new StreamsBuilder();

    	JsonbSerde<Homeward> homewardSerde = new JsonbSerde<>(Homeward.class);
    	
        WindowBytesStoreSupplier storeSupplier = 
        		Stores.persistentWindowStore(HOME_STORE, 
								        	 Duration.ofSeconds(300), 
								        	 Duration.ofSeconds(300), 
								        	 false);
        
		builder.stream(                                                       
                HOMEWARD_TOPIC,
                Consumed.with(Serdes.String(), homewardSerde)
        )
        .groupByKey(Grouped.with(Serdes.String(), homewardSerde))
    	.windowedBy(TimeWindows.of(Duration.ofSeconds(30)))
        .aggregate(
            Aggregation::new,
            (unmovedId, homeward, aggr) -> aggr.updateFrom(homeward),
            Materialized.<String, Aggregation> as(storeSupplier)
                    .withKeySerde(Serdes.String())
                    .withValueSerde(new JsonbSerde<>(Aggregation.class)))
        .toStream()
    	.print(Printed.toSysOut());
        
        return builder.build();
    }
}