package guru.bonacci.timesup.runforrest;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.context.ManagedExecutor;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.javadocmd.simplelatlng.LatLng;

import guru.bonacci.timesup.runforrest.client.TrackResponse;
import guru.bonacci.timesup.runforrest.client.TrackService;
import guru.bonacci.timesup.runforrest.model.RunConfig;
import guru.bonacci.timesup.runforrest.model.Track;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/runforrest")
public class RunForrestResource {

	@Inject ManagedExecutor executor;

	@Inject @RestClient TrackService trackService;
	@Inject JohnnieWalker johhnie;

	/**
		http POST localhost:8080/runforrest/me/Torpedo7/bike < src/main/resources/payload1.json
		http POST localhost:8080/runforrest/you/Warehouse/box < src/main/resources/payload20.json
		http POST localhost:8080/runforrest/him/NaturallyOrganic/coffee1 < src/main/resources/payload5.json
		http POST localhost:8080/runforrest/her/NaturallyOrganic/coffee2 < src/main/resources/payload10.json
		http POST localhost:8080/runforrest/us/Torpedo7/trampoline < src/main/resources/payload15.json
	*/
	@POST 
	@Path("/{moverId}/{unmovedId}/{trackingNumber}")
	@Consumes(MediaType.APPLICATION_JSON)
    public void run(@PathParam("moverId") String moverId, 
    				@PathParam("unmovedId") String unmovedId, 
    				@PathParam("trackingNumber") String trackingNumber, 
    				RunConfig runConfig) {
		log.info("run {} - {} - {} : {}", unmovedId, moverId, trackingNumber, runConfig); 
		TrackResponse trackResp = trackService.send(new Track(trackingNumber, moverId, unmovedId));

		executor.runAsync(() -> {
			executor.execute(() -> 
				johhnie.walk(moverId, new LatLng(trackResp.unmovedLat, trackResp.unmovedLon), runConfig));
		});
	}
}