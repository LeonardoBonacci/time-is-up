package guru.bonacci.timesup.homeward;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;

import guru.bonacci.timesup.homeward.joiners.TraceAverageJoiner;
import guru.bonacci.timesup.homeward.model.Homeward;
import guru.bonacci.timesup.homeward.model.Trace;
import io.quarkus.kafka.client.serialization.JsonbSerde;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class HomewardBoundTopology {

    static final String TRACE_TOPIC = "trace";
    static final String AVERAGER_TOPIC = "geohash-averager";
    static final String HOMEWARD_TOPIC = "homeward-bound";

    
    @Produces
    public Topology buildTopology() {
    	final StreamsBuilder builder = new StreamsBuilder();

        final KTable<String, Long> averageTable = builder.table(
            AVERAGER_TOPIC,
            Consumed.with(Serdes.String(), Serdes.Long())
        );

        // join a stream of traces with a table of average travel times
        builder.stream(                                                       
            TRACE_TOPIC,
            Consumed.with(Serdes.String(), new JsonbSerde<>(Trace.class))
        )
        .selectKey(
        	(k,v) -> v.moverGeohash + "/" + v.unmovedGeohash
        )
        .peek(
    		(k,v) -> log.info("Incoming trace... {}:{}", k, v)
        )
        .leftJoin(                                                        
    		averageTable,
    		new TraceAverageJoiner(),
    		Joined.with(Serdes.String(), new JsonbSerde<>(Trace.class), Serdes.Long())
        )
        .peek(
    		(k,v) -> log.info("Outgoing homeward... {}:{}", k, v)
        )
        .to(
        	HOMEWARD_TOPIC, 
        	Produced.with(Serdes.String(), new JsonbSerde<>(Homeward.class))
        );
        
        return builder.build();
    }
}