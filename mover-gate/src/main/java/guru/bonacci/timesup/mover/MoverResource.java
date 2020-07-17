package guru.bonacci.timesup.mover;

import java.util.concurrent.CompletionStage;

import javax.inject.Inject;
import javax.validation.ValidationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.metrics.annotation.Counted;

import guru.bonacci.timesup.mover.model.Mover;
import guru.bonacci.timesup.mover.produce.MoverProducer;

@Path("/mover")
@Consumes(MediaType.APPLICATION_JSON)
public class MoverResource {

	@Inject
	MoverProducer client;

	@POST
	@Counted(description = "Mover additions", absolute = true)
	public CompletionStage<Response> add(Mover mover) {
		return client.send(mover)
					 .thenApply(v -> Response.ok().build())
					 .exceptionally(throwable -> {
						if (throwable.getCause().getClass().equals(ValidationException.class)) {
							return Response.status(422).build();
						} else {
							return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
						}
					 }
		 );
	}
}