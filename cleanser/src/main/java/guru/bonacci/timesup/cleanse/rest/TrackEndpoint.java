package guru.bonacci.timesup.cleanse.rest;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("track")
@RegisterRestClient
public interface TrackEndpoint {

	@DELETE
	@Path("/{trackingNumber}")
	Response tombstone(@PathParam("trackingNumber") String trackingNumber);
}
