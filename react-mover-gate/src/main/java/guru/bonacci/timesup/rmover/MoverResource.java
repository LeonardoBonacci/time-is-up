package guru.bonacci.timesup.rmover;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import guru.bonacci.timesup.rmover.model.Mover;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/mover")
public class MoverResource {

	@Inject @Channel("mover-channel") Emitter<Mover> emitter;
	
	@POST //http POST localhost:30002/mover < src/main/resources/payload.json
	@Consumes(MediaType.APPLICATION_JSON)
    public void add(@Valid Mover mover) {
		log.info("add {}", mover); //TODO make trace logging
		emitter.send(KafkaRecord.of(mover.id, mover));
	}
	
	@POST
	@Path("/echo")
    @Produces(APPLICATION_JSON)
	@Consumes(APPLICATION_JSON)
    public String echo(String requestBody) {
        return requestBody;
    }
}