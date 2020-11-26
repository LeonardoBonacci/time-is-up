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
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.LatLngTool.Bearing;
import com.javadocmd.simplelatlng.util.LengthUnit;

import guru.bonacci.timesup.runforrest.client.MoverService;
import guru.bonacci.timesup.runforrest.client.TrackResponse;
import guru.bonacci.timesup.runforrest.client.TrackService;
import guru.bonacci.timesup.runforrest.model.Mover;
import guru.bonacci.timesup.runforrest.model.RunConfig;
import guru.bonacci.timesup.runforrest.model.Track;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/runforrest")
public class RunForrestResource {

	@Inject ManagedExecutor executor;

	@Inject @RestClient TrackService trackService;
	@Inject @RestClient MoverService moverService;

	@POST //http POST localhost:8080/runforrest/me/Torpedo/bike < src/main/resources/payload.json
	@Path("/{moverId}/{unmovedId}/{trackingNumber}")
	@Consumes(MediaType.APPLICATION_JSON)
    public void run(@PathParam("moverId") String moverId, 
    				@PathParam("unmovedId") String unmovedId, 
    				@PathParam("trackingNumber") String trackingNumber, 
    				RunConfig runConfig) {
		log.info("run {} - {} - {} : {}", unmovedId, moverId, trackingNumber, runConfig); 
		TrackResponse trackResp = trackService.send(new Track(trackingNumber, moverId, unmovedId));

		walk(moverId, new LatLng(trackResp.unmovedLat, trackResp.unmovedLon), runConfig, trackResp.unmovedLon);
	}
	
	int accelerationFactor = 5; //x times

	public void walk(String moverId, LatLng end, RunConfig config, double unmovedLon) {
		log.info("trying to reach {}", unmovedLon);

		LatLng start = LatLngTool.travel(end, Bearing.WEST, config.distanceMeters, LengthUnit.METER);
		double stepSizeMeters = config.speedKmHr * 1000 / 3600;
		
		log.info("stepsize meters per sec {}", stepSizeMeters);

		int stepSizeMetersAcc = (int)stepSizeMeters * accelerationFactor;
		//to satisfy the impatient demo audience we move a bit faster..
		log.info("accelerate to {}", stepSizeMetersAcc);

		step(new Mover(moverId, start.getLatitude(), start.getLongitude()), stepSizeMetersAcc, unmovedLon);
		return;
	}	
	
	public void step(Mover mover, int stepSizeMeters, double finalLon) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		mover.step(stepSizeMeters);
		if (mover.lon <= finalLon) {
			moverService.send(mover);
		} else {
			mover.lon = finalLon;
			log.info("arrival of {}", mover);
			moverService.send(mover);
			return;
		}
		
		step(mover, stepSizeMeters, finalLon);
	}

}