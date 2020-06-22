package guru.bonacci.timesup.unmoved.streams;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.Produced;

import guru.bonacci.timesup.model.TheTrack.Track;
import guru.bonacci.timesup.model.TheTrackGeo.TrackGeo;
import guru.bonacci.timesup.model.TheUnmovedGeo.UnmovedGeo;
import io.confluent.kafka.streams.serdes.protobuf.KafkaProtobufSerde;

@ApplicationScoped
public class TopologySupplier {

    private static final String UNMOVED_GEO_TOPIC = "unmoved_geo";
    private static final String TRACK_TOPIC = "track";
    private static final String TRACK_GEO_TOPIC = "track_geo";

    @Produces
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KafkaProtobufSerde<UnmovedGeo> unmovedSerde = new KafkaProtobufSerde<>(UnmovedGeo.class);
        KafkaProtobufSerde<Track> trackSerde = new KafkaProtobufSerde<>(Track.class);

        KafkaProtobufSerde<TrackGeo> trackGeoSerde = new KafkaProtobufSerde<>(TrackGeo.class);

        GlobalKTable<String, UnmovedGeo> unmoveds = builder.globalTable(
        		UNMOVED_GEO_TOPIC,
                Consumed.with(Serdes.String(), unmovedSerde));

        builder.stream(
                TRACK_TOPIC,
                Consumed.with(Serdes.String(), trackSerde))
                .join(
                        unmoveds,
                        (trackNumber, track) -> track.getUnmovedId(),
                        (track, unmoved) -> {
                            return TrackGeo.newBuilder()
                            			.setTrackingNumber(track.getTrackingNumber())
                            			.setMoverId(track.getMoverId())
                            			.setUnmovedId(track.getUnmovedId())
                            			.setUnmovedGeohash(unmoved.getGeohash()).build();
                        })
                .to(
                        TRACK_GEO_TOPIC,
                        Produced.with(Serdes.String(), trackGeoSerde));

        return builder.build();
    }
}