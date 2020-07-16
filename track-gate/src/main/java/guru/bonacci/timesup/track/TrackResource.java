package guru.bonacci.timesup.track;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import guru.bonacci.timesup.track.model.Track;
import guru.bonacci.timesup.track.produce.TrackProducer;

@Path("/track")
@Consumes(MediaType.APPLICATION_JSON)
public class TrackResource {

	@Inject TrackProducer client;
	

	@POST
    public Response add(Track track) {
    	client.send(track);
    	return Response.status(200).build();
    }
	
    @DELETE 
    @Path("/{nr}")
    public Response delete(@PathParam(value = "nr") String trackingNumber) {
    	client.tombstone(trackingNumber, 10000);
    	return Response.status(200).build();
    }
}