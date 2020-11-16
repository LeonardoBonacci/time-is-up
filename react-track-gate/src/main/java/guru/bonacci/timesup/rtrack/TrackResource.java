package guru.bonacci.timesup.rtrack;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import guru.bonacci.timesup.rtrack.model.Track;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/track")
public class TrackResource {

	@Inject @Channel("track-channel") Emitter<Track> emitter;
	
	@POST //http POST localhost:9091/track < src/main/resources/payload.json
	@Consumes(MediaType.APPLICATION_JSON)
    public void add(@Valid Track track) {
		log.info("add track {}", track);
		emitter.send(KafkaRecord.of(track.trackingNumber, track));
	}
	
	@DELETE //http DELETE localhost:8080/track/{id}
	@Path("/{id}")
    public void remove(@PathParam(value = "id") String trackingNumber) {
		log.info("remove track {}", trackingNumber);
		emitter.send(KafkaRecord.of(trackingNumber, null));
	}
}