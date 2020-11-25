package guru.bonacci.timesup.runmoved;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import guru.bonacci.timesup.runmoved.model.Unmoved;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/unmoved")
public class UnmovedResource {

	@Inject @Channel("unmoved-channel") Emitter<Unmoved> emitter;

	
	@POST //http POST localhost:30000/unmoved < src/main/resources/payload.json
	@Consumes(APPLICATION_JSON)
    public void add(@Valid Unmoved unmoved) {
		log.info("add {}", unmoved);
		emitter.send(KafkaRecord.of(unmoved.id, unmoved));
	}
	
	@DELETE //http DELETE localhost:30000/unmoved/{id}
	@Path("/{id}")
    public void remove(@PathParam(value = "id") String id) {
		log.info("remove Unmoved {}", id);
		emitter.send(KafkaRecord.of(id, null));
	}
	
	@POST
	@Path("/echo")
    @Produces(APPLICATION_JSON)
	@Consumes(APPLICATION_JSON)
    public String echo(String requestBody) {
        return requestBody;
    }
}