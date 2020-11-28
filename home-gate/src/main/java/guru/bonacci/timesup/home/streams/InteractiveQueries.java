package guru.bonacci.timesup.home.streams;

import static java.util.stream.StreamSupport.stream;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Collectors.toList;

import java.time.Instant;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;

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
import org.eclipse.microprofile.config.inject.ConfigProperty;

import guru.bonacci.timesup.home.model.Aggregation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class InteractiveQueries {

    @ConfigProperty(name = "hostname")
    String host;

    @Inject
    KafkaStreams streams;

    public List<PipelineMetadata> getMetaData() {
        return streams.allMetadataForStore(HomeTopology.HOME_STORE)
                .stream()
                .map(m -> new PipelineMetadata(
                        m.hostInfo().host() + ":" + m.hostInfo().port(),
                        m.topicPartitions()
                                .stream()
                                .map(TopicPartition::toString)
                                .collect(toSet())))
                .collect(toList());
    }

    public HomewardDataResult getData(String unmovedId) {
        var metadata = streams.queryMetadataForKey(
                HomeTopology.HOME_STORE,
                unmovedId,
                Serdes.String().serializer()
        );

        log.info("active host {}", metadata.getActiveHost().host());
        log.info("host {}", host);

        if (metadata == null || metadata == KeyQueryMetadata.NOT_AVAILABLE) {
            log.warn("Found no metadata for key {}", unmovedId);
            return HomewardDataResult.notFound();
        }
        else if (metadata.getActiveHost().host().equals(host)) {
            log.info("Found data for key {} locally", unmovedId);
            
            // for demo purposes we query the last 60 seconds (of several windows)
            KeyValueIterator<Long, Aggregation> windows = getStore().fetch(unmovedId, Instant.now().minusSeconds(60), Instant.now());
    		Aggregation result = 
    				stream(Spliterators.spliteratorUnknownSize(windows, Spliterator.ORDERED), false)
    				.map(keyValue -> keyValue.value)
    				.reduce(new Aggregation(), Aggregation::merge);

            if (result != null) {
                return HomewardDataResult.found(HomewardData.from(result));
            }
            else {
                return HomewardDataResult.notFound();
            }
        }
        else {
            log.info("Found data for key {} on remote host {}:{}", unmovedId, metadata.getActiveHost().host(), metadata.getActiveHost().port());
            return HomewardDataResult.foundRemotely(metadata.getActiveHost().host(), metadata.getActiveHost().port());
        }
    }

    private ReadOnlyWindowStore<String, Aggregation> getStore() {
        while (true) {
            try {
                return streams.store(StoreQueryParameters.fromNameAndType(HomeTopology.HOME_STORE, QueryableStoreTypes.windowStore()));
            } catch (InvalidStateStoreException e) {
                // ignore, store not ready yet
            }
        }
    }
}
