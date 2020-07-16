package guru.bonacci.timesup.cleanse.rest;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient
@Path("track")
public interface TrackEndpoint {

	@DELETE
	@Path("/{nr}/delay/60000")
	Response tombstone(@PathParam("nr") String trackingNumber);
}
