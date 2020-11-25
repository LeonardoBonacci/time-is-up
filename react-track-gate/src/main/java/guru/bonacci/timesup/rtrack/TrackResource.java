package guru.bonacci.timesup.rtrack;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import guru.bonacci.timesup.rtrack.model.Track;
import guru.bonacci.timesup.rtrack.model.Unmoved;
import guru.bonacci.timesup.rtrack.streams.UnmovedTopology;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@Path("/track")
public class TrackResource {

	@Inject
	@Channel("track-channel")
	Emitter<Track> emitter;

	
	@Inject KafkaStreams streams;
	ReadOnlyKeyValueStore<String, Unmoved> unmovedStore;
    @ConfigProperty(name = "geohash.length", defaultValue = "8") Integer geoHashLength;

    
	@PostConstruct
	void init(){
		unmovedStore = streams.store(
				StoreQueryParameters.fromNameAndType(UnmovedTopology.UNMOVED_STORE, QueryableStoreTypes.keyValueStore()));
    }

	
	@POST // http POST localhost:30001/track < src/main/resources/payload.json
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response add(@Valid Track track) {
		log.info("add {}", track);

		var unmoved = unmovedStore.get(track.unmovedId);
		
		if (unmoved != null) {
			log.info("enrich track with {}", unmoved);
			emitter.send(KafkaRecord.of(track.trackingNumber, track.enrich(unmoved, geoHashLength)));
			return Response.ok(track.trackingNumber).build();
		} else {
			var warning = "No unmoved found for id " + track.unmovedId;
			log.warn(warning);
			return Response.status(Status.GONE.getStatusCode(), warning).build();
		}

	}

	@DELETE // http DELETE localhost:30001/track/{id}
	@Path("/{id}")
	public void remove(@PathParam(value = "id") String trackingNumber) {
		log.info("remove Track {}", trackingNumber);
		emitter.send(KafkaRecord.of(trackingNumber, null));
	}

	@POST
	@Path("/echo")
	@Produces(APPLICATION_JSON)
	@Consumes(APPLICATION_JSON)
	public String echo(String requestBody) {
		return requestBody;
	}
}