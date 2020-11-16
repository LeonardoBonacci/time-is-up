package guru.bonacci.timesup.rmover;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import guru.bonacci.timesup.rmover.model.Mover;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;

@Path("/mover")
public class MoverResource {

	@Inject @Channel("mover-channel") Emitter<Mover> emitter;
	
	@POST //http POST localhost:8080/mover < src/main/resources/payload.json
	@Consumes(MediaType.APPLICATION_JSON)
    public void add(@Valid Mover mover) {
		emitter.send(KafkaRecord.of(mover.id, mover));
	}
}