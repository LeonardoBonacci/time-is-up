package guru.bonacci.timesup.runforrest.client;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import guru.bonacci.timesup.runforrest.model.Track;

@Path("/track")
@RegisterRestClient
public interface TrackService {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    TrackResponse send(Track track);
}