package guru.bonacci.timesup.unmoved;

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

import guru.bonacci.timesup.unmoved.model.Unmoved;
import guru.bonacci.timesup.unmoved.produce.UnmovedProducer;

@Path("/unmoved")
@Consumes(MediaType.APPLICATION_JSON)
public class UnmovedResource {

	private final Logger log = Logger.getLogger(UnmovedResource.class);

	@Inject UnmovedProducer client;
	

	@POST
	@Timeout(1000)
	@Counted(description = "Unmoved additions", absolute = true)
	@CircuitBreaker
	@Fallback(fallbackMethod = "fallbackAdd")
	public Response add(Unmoved unmoved) {
		log.infof("Adding %s", unmoved);

		try {
			client.send(unmoved);
	    	return Response.status(200).build();
		} catch (ValidationException e) {
	    	return Response.status(422).build();
	    }
    }
    
	public Response fallbackAdd(Unmoved unmoved) {
        log.info("Falling back add");
        return Response.status(503).build();
    }

	@DELETE 
    @Path("/{id}")
	@Timeout(1000)
	@Fallback(fallbackMethod = "fallbackDel")
	@CircuitBreaker(failOn = InterruptException.class)
	@Counted(description = "Unmoved deletes", absolute = true)
    public Response del(@PathParam(value = "id") String unmovedId) {
    	log.infof("Deleting %s", unmovedId);

    	client.tombstone(unmovedId);
    	return Response.status(200).build();
    }
	
	public Response fallbackDel(String unmovedId) {
        log.info("Falling back del");
        return Response.status(503).build();
    }
}