package guru.bonacci.timesup.rtrack;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import io.smallrye.reactive.messaging.kafka.KafkaRecord;

@Path("/track")
public class TrackResource {

	@Inject @Channel("track-channel") Emitter<Track> emitter;
	
	@POST //http POST localhost:8080/track < src/main/resources/payload.json
	@Consumes(MediaType.APPLICATION_JSON)
    public void add(@Valid Track track) {
		emitter.send(KafkaRecord.of(track.trackingNumber, track));
	}
}