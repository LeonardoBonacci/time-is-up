package guru.bonacci.timesup.source;

import org.apache.commons.lang3.tuple.Pair;

public class StepSimulator {
	
	private static float fromLat = -1.00f;
	private static float fromLon = -1.00f;
	private static float toLat = 1.00f;
	private static float toLon = 1.00f;

	private static float distanceLat = Math.abs(toLat - fromLat);
	private static float distanceLon = Math.abs(toLon - fromLon);

    static Pair<Float, Float> stepMover(int nrSteps, int i) {
    	final int stepNr = i + 1;
    	final float stepSizeLat = distanceLat / nrSteps;
    	final float stepSizeLon = distanceLon / nrSteps;

    	final float nextLat = fromLat + stepNr*stepSizeLat;
    	final float nextLon = fromLon + stepNr*stepSizeLon;

    	return Pair.of(nextLat, nextLon);
    }
    
    static Pair<Float, Float> stepUnmoved() {
    	return Pair.of(0.99f, 0.99f);
    }
}

