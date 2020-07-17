package guru.bonacci.timesup.home.streams;

import java.time.Duration;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.state.Stores;
import org.apache.kafka.streams.state.WindowBytesStoreSupplier;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import guru.bonacci.kafka.serialization.JacksonSerde;
import guru.bonacci.timesup.home.model.Trace;
import guru.bonacci.timesup.home.model.UnmovedAggr;


@ApplicationScoped
public class TopologySupplier {

	private final Logger log = Logger.getLogger(TopologySupplier.class);
	
    public static final String STORE = "unmoved-store";

	@ConfigProperty(name = "kafka.topic", defaultValue = "homeward") 
	String topic;

	
    @Produces
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        Serde<Trace> traceSerde = JacksonSerde.of(Trace.class);
        Serde<UnmovedAggr> aggrSerde = JacksonSerde.of(UnmovedAggr.class, true);
        
        WindowBytesStoreSupplier storeSupplier = 
        		Stores.persistentWindowStore(STORE, 
								        	 Duration.ofSeconds(300), 
								        	 Duration.ofSeconds(300), 
								        	 false);
        builder.stream(
                topic,
                Consumed.with(Serdes.String(), traceSerde)
            )
        	.peek((k,v) -> log.infof("%s<before>%s", k, v))
        	.selectKey((key, value) -> value.UNMOVED_ID) 
        	.groupByKey(Grouped.with(Serdes.String(), traceSerde))
        	.windowedBy(TimeWindows.of(Duration.ofSeconds(30)))
            .aggregate( 
                    UnmovedAggr::new,
                    (unmovedId, trace, aggr) -> aggr.updateFrom(trace),
                    Materialized.<String, UnmovedAggr> as(storeSupplier)
                        .withKeySerde(Serdes.String())
                        .withValueSerde(aggrSerde)
            )
            .toStream()
        	.peek((k,v) -> log.infof("%s<after>%s", k, v));
        return builder.build();
    }
}