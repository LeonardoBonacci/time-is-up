package guru.bonacci.timesup.track;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import guru.bonacci.timesup.track.model.Track;

@Path("/track")
@Consumes(MediaType.APPLICATION_JSON)
public class TrackResource {

	@Inject TrackProducer kClient;
	
    @POST
    public Response send(Track track) {
    	kClient.send(track);
    	return Response.status(200).build();
    }
}