package guru.bonacci.timesup.runforrest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.LatLngTool.Bearing;
import com.javadocmd.simplelatlng.util.LengthUnit;

import guru.bonacci.timesup.runforrest.client.MoverService;
import guru.bonacci.timesup.runforrest.model.Mover;
import guru.bonacci.timesup.runforrest.model.RunConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestScoped
public class JohnnieWalker {

	static final int ACCELERATION_FACTOR = 5; //x times


	@Inject @RestClient MoverService moverService;

	
	void walk(String moverId, LatLng end, RunConfig config) {
		var unmovedLon = end.getLongitude();
		log.info("trying to reach {}", unmovedLon);

		var start = LatLngTool.travel(end, Bearing.WEST, config.distanceMeters, LengthUnit.METER);
		var stepSizeMeters = config.speedKmHr * 1000 / 3600;
		
		log.info("stepsize meters per sec {}", stepSizeMeters);

		var stepSizeMetersAcc = (int)stepSizeMeters * ACCELERATION_FACTOR;
		//to satisfy the impatient demo audience we move a bit faster..
		log.info("accelerate to {}", stepSizeMetersAcc);

		step(new Mover(moverId, start.getLatitude(), start.getLongitude()), stepSizeMetersAcc, unmovedLon);
		return;
	}	
	
	private void step(Mover mover, int stepSizeMeters, double finalLon) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		mover.step(stepSizeMeters);
		if (mover.lon <= finalLon) {
			log.info("from {} to {}", mover.lon, finalLon);
			moverService.send(mover);
		} else {
			mover.lon = finalLon;
			log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			log.info("I have arrived {}", mover);
			log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			moverService.send(mover);
			return;
		}
		
		step(mover, stepSizeMeters, finalLon);
	}

}
