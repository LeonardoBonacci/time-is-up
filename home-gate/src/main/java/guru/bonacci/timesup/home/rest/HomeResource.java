package guru.bonacci.timesup.home.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import guru.bonacci.timesup.home.streams.InteractiveQueries;
import guru.bonacci.timesup.home.streams.PipelineMetadata;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@Path("/home")
public class HomeResource {

	@Inject InteractiveQueries interactiveQueries;


	@GET //http --follow localhost:30004/home/Warehouse
	@Path("/data/{unmovedId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response traces(@PathParam("unmovedId") String unmovedId) {
		var result = interactiveQueries.getData(unmovedId);

		if (result.getResult().isPresent()) {
			return Response.ok(result.getResult().get()).build();
		} else if (result.getHost().isPresent()) {
			var otherUri = getOtherUri(result.getHost().get(), result.getPort().getAsInt(), unmovedId);
			return Response.seeOther(otherUri).build();
		} else {
			return Response.status(Status.NOT_FOUND.getStatusCode(), "No data found for unmoved " + unmovedId).build();
		}
	}

	public Response fallbackTraces(String unmovedId) {
        log.info("Falling back traces");
        return Response.status(503).build();
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