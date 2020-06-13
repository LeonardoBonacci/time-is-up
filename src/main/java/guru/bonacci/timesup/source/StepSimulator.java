package guru.bonacci.timesup.source;

import org.apache.commons.lang3.tuple.Pair;

public class StepSimulator {
	
    static float fromLat = -36.661281f;
    static float fromLon = 174.743549f;
    static float toLat = -36.801947f;
    static float toLon = 174.758768f;

    static float distanceLat = Math.abs(toLat - fromLat);
    static float distanceLon = Math.abs(toLon - fromLon);

    static float stepSizeLat = distanceLat / 20;;
    static float stepSizeLon = distanceLon / 20;;

    static Pair<Float, Float> stepMover(long counter) {
    	long count = counter % 20;
    	
//    	float stepDev = new Random().nextFloat();
//    	float nextLatStep = fromLat + count*(stepDev*stepSizeLat);
//    	float nextLonStep = fromLon + count*(stepDev*stepSizeLon);

    	float nextLatStep = fromLat + count*stepSizeLat;
    	float nextLonStep = fromLon + count*stepSizeLon;

    	return Pair.of(nextLatStep % distanceLat, nextLonStep % distanceLon);
    }
    
    static Pair<Float, Float> unmoved(long counter) {
        float stepSizeLat = distanceLat / 5;;
        float stepSizeLon = distanceLon / 5;;

        long count = counter % 5;
    	
    	float nextLatStep = fromLat + count*stepSizeLat;
    	float nextLonStep = fromLon + count*stepSizeLon;

    	return Pair.of(nextLatStep % distanceLat, nextLonStep % distanceLon);
    }
}

