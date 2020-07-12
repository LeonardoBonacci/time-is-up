package guru.bonacci.timesup.home;

import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import guru.bonacci.timesup.home.model.Trace;
import guru.bonacci.timesup.home.produce.HomeConsumer;
import io.smallrye.mutiny.Multi;

@Path("/home")
@Produces(MediaType.APPLICATION_JSON)
public class HomeResource {

	@Inject HomeConsumer ksqlClient;
	
	
	@GET @Path("/{unmovedId}")
	@Produces(MediaType.SERVER_SENT_EVENTS)
    public Multi<Trace> traces(@PathParam(value = "unmovedId") String unmovedId) throws InterruptedException, ExecutionException {
		return ksqlClient.traces(unmovedId);
    }
	
	@GET
	@Produces(MediaType.SERVER_SENT_EVENTS)
	public Multi<String> stream() {
	    return Multi.createFrom().items("a","b","c");
	}
}