package guru.bonacci.timesup.track;

import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import guru.bonacci.timesup.track.produce.MoverProducer;

@Path("/mover")
@Consumes(MediaType.APPLICATION_JSON)
public class MoverResource {

	@Inject MoverProducer ksqlClient;
	
    @POST
    public Response ping() throws InterruptedException, ExecutionException {
    	ksqlClient.ping();
    	return Response.status(200).build();
    }
}