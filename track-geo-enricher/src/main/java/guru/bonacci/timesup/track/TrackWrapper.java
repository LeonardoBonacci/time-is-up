package guru.bonacci.timesup.track;

import guru.bonacci.timesup.model.TheMover;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter @Getter
@RequiredArgsConstructor
public class TrackWrapper {

	private final Tracks tracks;
	private final TheMover mover;
}
