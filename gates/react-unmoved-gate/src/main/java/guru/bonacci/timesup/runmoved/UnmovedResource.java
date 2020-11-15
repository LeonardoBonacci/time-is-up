package guru.bonacci.timesup.runmoved;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.reactivestreams.Publisher;

import io.smallrye.reactive.messaging.kafka.KafkaRecord;

@Path("/unmoved")
public class UnmovedResource {

	@Inject @Channel("unmoved-channel") Emitter<Unmoved> emitter;
	
	@POST //http POST localhost:8080/track < src/main/resources/payload.json
	@Consumes(MediaType.APPLICATION_JSON)
    public void add(@Valid Unmoved unmoved) {
		emitter.send(KafkaRecord.of(unmoved.id, unmoved));
	}
	
	
	@Inject @Channel("unmoved-data-stream") Publisher<Unmoved> asStream; 

	@GET //http --stream -f localhost:8080/unmoved/stream
    @Path("/stream")
    @Produces(MediaType.SERVER_SENT_EVENTS) 
    public Publisher<Unmoved> stream() { 
        return asStream;
    }
}