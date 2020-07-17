package guru.bonacci.timesup.track;

import javax.inject.Inject;
import javax.validation.ValidationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.kafka.common.errors.InterruptException;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.jboss.logging.Logger;

import guru.bonacci.timesup.track.model.Track;
import guru.bonacci.timesup.track.produce.TrackProducer;

@Path("/track")
@Consumes(MediaType.APPLICATION_JSON)
public class TrackResource {

	private final Logger log = Logger.getLogger(TrackResource.class);

	@Inject TrackProducer client;
	

	@POST
	@Timeout(250)
	@Counted(description = "Track additions", absolute = true)
	@CircuitBreaker(failOn = InterruptException.class)
	@Fallback(fallbackMethod = "fallbackAdd")
    public Response add(Track track) {
		log.infof("Adding %s", track);

		try {
			client.send(track);
			return Response.status(200).build();
		} catch (ValidationException e) {
			return Response.status(422).build();
		}
    }

	public Response fallbackAdd(Track track) {
        log.info("Falling back add");
        return Response.status(503).build();
    }

    @DELETE 
    @Path("/{nr}")
	@Timeout(250)
	@Counted(description = "Track deletes", absolute = true)
	@CircuitBreaker(failOn = InterruptException.class)
	@Fallback(fallbackMethod = "fallbackDel")
    public Response delete(@PathParam(value = "nr") String trackingNumber) {
    	log.infof("Deleting %s", trackingNumber);

    	client.tombstone(trackingNumber, 1);
		return Response.status(200).build();
    }

	public Response fallbackDel(String nr) {
        log.info("Falling back del");
        return Response.status(503).build();
    }

    @DELETE 
    @Path("/{nr}/delay/{ms}")
    public Response delete(@PathParam(value = "nr") String trackingNumber, @PathParam(value = "ms") long delay) {
    	log.infof("Deleting %s with delay %s ms", trackingNumber, delay);

    	client.tombstone(trackingNumber, delay);
		return Response.status(200).build();
    }
    
	public Response fallbackDel(String nr, String delay) {
        log.info("Falling back del");
        return Response.status(503).build();
    }
}