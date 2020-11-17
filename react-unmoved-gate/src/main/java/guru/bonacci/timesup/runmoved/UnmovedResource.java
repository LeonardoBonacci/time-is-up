package guru.bonacci.timesup.runmoved;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.reactivestreams.Publisher;

import guru.bonacci.timesup.runmoved.model.Unmoved;
import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/unmoved")
public class UnmovedResource {

	@Inject @Channel("unmoved-channel") Emitter<Unmoved> emitter;
	@Inject @Channel("unmoved-data-stream") Multi<Unmoved> asStream; 

	@POST //http POST localhost:9090/unmoved < src/main/resources/payload.json
	@Consumes(MediaType.APPLICATION_JSON)
    public void add(@Valid Unmoved unmoved) {
		log.info("add unmoved {}", unmoved);
		emitter.send(KafkaRecord.of(unmoved.id, unmoved));
	}
	
	@DELETE //http DELETE localhost:9090/unmoved/{id}
	@Path("/{id}")
    public void remove(@PathParam(value = "id") String id) {
		log.info("remove unmoved {}", id);
		emitter.send(KafkaRecord.of(id, null));
	}

	
	
	@GET //http --stream -f localhost:9090/unmoved/stream
    @Path("/stream")
    @Produces(MediaType.SERVER_SENT_EVENTS) 
    public Publisher<Unmoved> stream() { 
		return asStream;
	}
	
	@GET //http --stream -f localhost:9090/unmoved/stream2
    @Path("/stream2")
    @Produces(MediaType.SERVER_SENT_EVENTS) 
    public Publisher<Unmoved> stream2() { 
		return asStream.filter(r -> true);
	}
}