package guru.bonacci.timesup.rhome;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.jboss.resteasy.annotations.SseElementType;
import org.reactivestreams.Publisher;

import guru.bonacci.timesup.rhome.model.Homeward;
import io.smallrye.mutiny.Multi;

@Path("/home")
public class HomeResource {

	@Inject @Channel("homeward-stream") Multi<Homeward> homewardStream; 

	@GET //http --stream -f localhost:30003/home/stream/nowhere
    @Path("/stream/{unmovedId}")
	@SseElementType(MediaType.APPLICATION_JSON) 
    @Produces(MediaType.SERVER_SENT_EVENTS) 
    public Publisher<Homeward> stream(@PathParam(value = "unmovedId") String unmovedId) { 
		return homewardStream.filter(homeward -> unmovedId.equals(homeward.unmovedId));
	}
}