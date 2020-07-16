package guru.bonacci.timesup.unmoved;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import guru.bonacci.timesup.unmoved.model.Unmoved;
import guru.bonacci.timesup.unmoved.produce.UnmovedProducer;

@Path("/unmoved")
@Consumes(MediaType.APPLICATION_JSON)
public class UnmovedResource {

	@Inject UnmovedProducer client;
	

	@POST
    public Response add(Unmoved unmoved) {
    	client.send(unmoved);
    	return Response.status(200).build();
    }
    
    @DELETE 
    @Path("/{id}")
    public Response delete(@PathParam(value = "id") String unmovedId) {
    	client.tombstone(unmovedId);
    	return Response.status(200).build();
    }
}