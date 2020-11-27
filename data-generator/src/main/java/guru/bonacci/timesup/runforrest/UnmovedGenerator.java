package guru.bonacci.timesup.runforrest;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Outgoing;

import com.google.common.collect.ImmutableMap;

import guru.bonacci.timesup.runforrest.model.Unmoved;
import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class UnmovedGenerator {

	static final String STORE_T7 = "Torpedo7";
	static final String STORE_WH = "Warehouse";
	static final String STORE_WS = "WarehouseStationary";
	static final String STORE_NO = "NaturallyOrganic";

	Map<String, Unmoved> stores = ImmutableMap.<String, Unmoved> builder()
	    		.put(STORE_T7, new Unmoved(STORE_T7, -36.731882, 74.707943))
	    		.put(STORE_WH, new Unmoved(STORE_WH, -36.729784, 74.704225))
	    		.put(STORE_WS, new Unmoved(STORE_WS, -36.730803, 74.706022))
	    		.put(STORE_NO, new Unmoved(STORE_NO, -36.730223, 74.710662))
		      .build();


    @Outgoing("unmoved-channel")                                          
    public Multi<KafkaRecord<String, Unmoved>> sendStores() {
        return Multi.createFrom().items(stores.values().stream()
    		.peek(s -> log.info(s.toString()))
            .map(s -> KafkaRecord.of(s.id, s))
        );
    }
}