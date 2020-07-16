package guru.bonacci.timesup.track;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;

import guru.bonacci.timesup.track.model.Track;
import guru.bonacci.timesup.track.produce.TrackProducer;

@Path("/track")
@Consumes(MediaType.APPLICATION_JSON)
public class TrackResource {

	private final Logger log = Logger.getLogger(TrackResource.class);

	@Inject TrackProducer client;
	

	@POST
    public Response add(Track track) {
		log.infof("Adding %s", track);

		client.send(track);
    	return Response.status(200).build();
    }
	
    @DELETE 
    @Path("/{nr}")
    public Response delete(@PathParam(value = "nr") String trackingNumber) {
    	log.infof("Deleting %s", trackingNumber);

    	client.tombstone(trackingNumber, 1);
		return Response.status(200).build();
    }
    
    @DELETE 
    @Path("/{nr}/delay/{ms}")
    public Response delete(@PathParam(value = "nr") String trackingNumber, @PathParam(value = "ms") long delay) {
    	log.infof("Deleting %s with delay %s ms", trackingNumber, delay);

    	client.tombstone(trackingNumber, delay);
		return Response.status(200).build();
    }
}