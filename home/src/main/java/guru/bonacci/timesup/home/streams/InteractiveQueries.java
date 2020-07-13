package guru.bonacci.timesup.home.streams;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.apache.kafka.streams.state.StreamsMetadata;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.google.common.collect.Streams;

import guru.bonacci.timesup.home.consume.SinkTopologyProducer;
import guru.bonacci.timesup.home.model.UnmovedAggr;
import guru.bonacci.timesup.home.rest.UnmovedDataResult;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class InteractiveQueries {


	@ConfigProperty(name="hostname")
    String host;

    @Inject
    KafkaStreams streams;

    
    public List<PipelineMetadata> getMetaData() {
        return streams.allMetadataForStore(SinkTopologyProducer.STORE)
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
        StreamsMetadata metadata = streams.metadataForKey(
                SinkTopologyProducer.STORE,
                unmovedId,
                Serdes.String().serializer()
        );

        if (metadata == null || metadata == StreamsMetadata.NOT_AVAILABLE) {
            log.warn("Found no metadata for key {}", unmovedId);
            return UnmovedDataResult.notFound();
        }
        else if (metadata.host().equals(host)) {
            log.info("Found data for key {} locally", unmovedId);
            
            // for demo purposes we query the last three seconds (of several windows)
            // this way trains don't cling to stations when they have passed
            KeyValueIterator<Long, UnmovedAggr> result1 = getStore().fetch(unmovedId, Instant.now().minusSeconds(2), Instant.now());
            UnmovedAggr result = Streams.stream(result1).map(keyValue -> keyValue.value).reduce(new UnmovedAggr(), UnmovedAggr::merge);

            if (result != null) {
                return UnmovedDataResult.found(UnmovedData.from(result));
            }
            else {
                return UnmovedDataResult.notFound();
            }
        }
        else {
            log.info("Found data for key {} on remote host {}:{}", unmovedId, metadata.host(), metadata.port());
            return UnmovedDataResult.foundRemotely(metadata.host(), metadata.port());
        }
    }

    private ReadOnlyWindowStore<String, UnmovedAggr> getStore() {
        while (true) {
            try {
                return streams.store(SinkTopologyProducer.STORE, QueryableStoreTypes.windowStore());
            } catch (InvalidStateStoreException e) {
                // ignore, store not ready yet
            }
        }
    }
}