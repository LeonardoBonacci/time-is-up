package guru.bonacci.timesup.track.streams;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import guru.bonacci.kafka.serialization.JacksonSerde;
import guru.bonacci.timesup.model.TheMover.Mover;
import guru.bonacci.timesup.model.TheTrace.Trace;
import guru.bonacci.timesup.model.TheTrackGeo.TrackGeo;
import guru.bonacci.timesup.track.Tracks;
import io.confluent.kafka.streams.serdes.protobuf.KafkaProtobufSerde;

@ApplicationScoped
public class TopologySupplier {

    private static final String TRACK_GEO_TOPIC = "track_geo";
    private static final String MOVER_TOPIC = "mover";
    private static final String TRACE_TOPIC = "trace";

    @Produces
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KafkaProtobufSerde<Mover> moverSerde = new KafkaProtobufSerde<>(Mover.class);
        KafkaProtobufSerde<TrackGeo> trackSerde = new KafkaProtobufSerde<>(TrackGeo.class);
        KafkaProtobufSerde<Trace> traceSerde = new KafkaProtobufSerde<>(Trace.class);

        ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        final Serde<Tracks> tracksSerde = JacksonSerde.of(Tracks.class, mapper);
        
        KStream<String, TrackGeo> trackStream = builder.stream(
        		TRACK_GEO_TOPIC,
                Consumed.with(Serdes.String(), trackSerde));

        KTable<String, Tracks> trackTable = trackStream
                .groupByKey()
                .aggregate(Tracks::new
                        , (key, track, tracks) -> tracks.add(track)
                        , Materialized.with(Serdes.String(), tracksSerde)
                );
        
        builder.stream(
                MOVER_TOPIC,
                Consumed.with(Serdes.String(), moverSerde))
        		.join(
        				trackTable, 
                        (mover, track) -> {
                            return new TrackWrapper();
                        })
        		.flatMapValues(new ValueMapper<TrackWrapper, Iterable<Trace>> {
        		     Iterable<Trace> apply(TrackWrapper wrapper) {
        		         return null; //for each Track a Trace
        		     }
        		 })
                .to(
                        TRACE_TOPIC,
                        Produced.with(Serdes.String(), traceSerde));

        return builder.build();
    }
}