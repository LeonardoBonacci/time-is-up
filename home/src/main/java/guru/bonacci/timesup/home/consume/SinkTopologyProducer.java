package guru.bonacci.timesup.home.consume;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;


@ApplicationScoped
public class SinkTopologyProducer {

    public static final String STORE = "unmoved-store";

    
    @Produces
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        //TODO

        return builder.build();
    }
}