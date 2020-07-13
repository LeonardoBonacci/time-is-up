package guru.bonacci.timesup.home.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import guru.bonacci.timesup.home.consume.HomeConsumer;
import guru.bonacci.timesup.home.model.Trace;
import guru.bonacci.timesup.home.streams.InteractiveQueries;
import guru.bonacci.timesup.home.streams.PipelineMetadata;
import io.smallrye.mutiny.Multi;

@ApplicationScoped
@Path("/home")
public class HomeResource {

	@Inject InteractiveQueries interactiveQueries;
	@Inject HomeConsumer ksqlClient;

	@GET
	@Path("/stream/{unmovedId}")
	@Produces(MediaType.SERVER_SENT_EVENTS)
	public Multi<Trace> stream(@PathParam("unmovedId") String unmovedId)
			throws InterruptedException, ExecutionException {
		return ksqlClient.traces(unmovedId);
	}

	@GET
	@Path("/{unmovedId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response traces(@PathParam("unmovedId") String unmovedId) {
		UnmovedDataResult result = interactiveQueries.getData(unmovedId);

		if (result.getResult().isPresent()) {
			return Response.ok(result.getResult().get()).build();
		} else if (result.getHost().isPresent()) {
			URI otherUri = getOtherUri(result.getHost().get(), result.getPort().getAsInt(), unmovedId);
			return Response.seeOther(otherUri).build();
		} else {
			return Response.status(Status.NOT_FOUND.getStatusCode(), "No data found for unmoved " + unmovedId).build();
		}
	}

	@GET
	@Path("/meta-data")
	@Produces(MediaType.APPLICATION_JSON)
	public List<PipelineMetadata> getMetaData() {
		return interactiveQueries.getMetaData();
	}

	private URI getOtherUri(String host, int port, String unmovedId) {
		try {
			return new URI("http://" + host + ":" + port + "/home/" + unmovedId);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
}