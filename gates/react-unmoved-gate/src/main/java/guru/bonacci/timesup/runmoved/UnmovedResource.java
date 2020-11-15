package guru.bonacci.timesup.runmoved;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import io.smallrye.reactive.messaging.kafka.KafkaRecord;

@Path("/unmoved")
public class UnmovedResource {

	@Inject @Channel("unmoved-channel") Emitter<Unmoved> emitter;
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
    public void create(Unmoved unmoved) {
		emitter.send(KafkaRecord.of(unmoved.id, unmoved));
	}
}