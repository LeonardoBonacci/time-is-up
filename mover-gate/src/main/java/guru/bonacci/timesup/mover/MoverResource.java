package guru.bonacci.timesup.mover;

import java.util.concurrent.CompletionStage;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.kafka.common.errors.InterruptException;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.jboss.logging.Logger;

import guru.bonacci.timesup.mover.model.Mover;
import guru.bonacci.timesup.mover.produce.MoverProducer;

@Path("/mover")
@Consumes(MediaType.APPLICATION_JSON)
public class MoverResource {

	private final Logger log = Logger.getLogger(MoverResource.class);

	@Inject MoverProducer client;
	
    @POST
	@Timeout(250)
	@Counted(description = "Mover additions", absolute = true)
	@CircuitBreaker(failOn = InterruptException.class, delay = 60000, requestVolumeThreshold = 10)
	@Fallback(fallbackMethod = "fallbackAdd")
    public CompletionStage<Void> add(Mover mover) {
		return client.send(mover);
    }
    
	public Response fallbackAdd(Mover mover) {
        log.info("Falling back add");
        return Response.status(503).build();
    }
}