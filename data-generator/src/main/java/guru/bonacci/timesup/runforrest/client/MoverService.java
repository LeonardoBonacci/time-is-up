package guru.bonacci.timesup.runforrest.client;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import guru.bonacci.timesup.runforrest.model.Mover;

@Path("/mover")
@RegisterRestClient
public interface MoverService {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    void send(Mover mover);
}