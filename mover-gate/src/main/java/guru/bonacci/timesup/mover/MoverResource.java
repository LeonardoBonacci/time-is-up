package guru.bonacci.timesup.mover;

import java.util.concurrent.CompletionStage;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import guru.bonacci.timesup.mover.model.Mover;
import guru.bonacci.timesup.mover.produce.MoverProducer;

@Path("/mover")
@Consumes(MediaType.APPLICATION_JSON)
public class MoverResource {


	@Inject MoverProducer client;
	
    @POST
    public CompletionStage<Void> add(Mover mover) {
		return client.send(mover);
    }
}