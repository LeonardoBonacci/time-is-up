package guru.bonacci.timesup.home.streams;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyQueryMetadata;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;

import com.google.common.collect.Streams;

import guru.bonacci.timesup.home.model.UnmovedAggr;
import guru.bonacci.timesup.home.rest.UnmovedDataResult;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class InteractiveQueries {


//TODO	@ConfigProperty(name = "hostname")
    String host = "localhost";

    @Inject
    KafkaStreams streams;

    
    public List<PipelineMetadata> getMetaData() {
        return streams.allMetadataForStore(TopologyProducer.STORE)
                .stream()
                .map(m -> new PipelineMetadata(
                        m.hostInfo().host() + ":" + m.hostInfo().port(),
                        m.topicPartitions()
                            .stream()
                            .map(TopicPartition::toString)
                            .collect(Collectors.toSet()))
                )
                .collect(Collectors.toList());
    }

    public UnmovedDataResult getData(String unmovedId) {
        KeyQueryMetadata metadata = streams.queryMetadataForKey(
                TopologyProducer.STORE,
                unmovedId,
                Serdes.String().serializer()
        );

        if (metadata == null || metadata == KeyQueryMetadata.NOT_AVAILABLE) {
            log.warn("Found no metadata for key {}", unmovedId);
            return UnmovedDataResult.notFound();
        }
        else if (metadata.getActiveHost().host().equals(host)) {
            log.info("Found data for key {} locally", unmovedId);
            
            // for demo purposes we query the last 30 seconds (of several windows)
            KeyValueIterator<Long, UnmovedAggr> windows = getStore().fetch(unmovedId, Instant.now().minusSeconds(30), Instant.now());
            UnmovedAggr result = Streams.stream(windows).map(keyValue -> keyValue.value).reduce(new UnmovedAggr(), UnmovedAggr::merge);

            if (result != null) {
                return UnmovedDataResult.found(UnmovedData.from(result));
            }
            else {
                return UnmovedDataResult.notFound();
            }
        }
        else {
//            log.info("Found data for key {} on remote host {}:{}", unmovedId, metadata.host(), metadata.port());
//            return UnmovedDataResult.foundRemotely(metadata.host(), metadata.port());
        	return null; //TODO
        }
    }

    private ReadOnlyWindowStore<String, UnmovedAggr> getStore() {
        while (true) {
            try {
                return streams.store(StoreQueryParameters.fromNameAndType(TopologyProducer.STORE, QueryableStoreTypes.windowStore()));
            } catch (InvalidStateStoreException e) {
                // ignore, store not ready yet
            }
        }
    }
}