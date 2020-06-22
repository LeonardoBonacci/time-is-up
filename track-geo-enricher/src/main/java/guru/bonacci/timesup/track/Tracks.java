package guru.bonacci.timesup.track;

import java.util.HashSet;
import java.util.Set;

import guru.bonacci.timesup.model.TheTrackGeo.TrackGeo;
import lombok.ToString;

@ToString
public class Tracks {

	private Set<TrackGeo> tracks = new HashSet<>();

    public Tracks add(TrackGeo track) {
        tracks.add(track);
        return this;
    }
}