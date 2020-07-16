package guru.bonacci.timesup.cleanse.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trace {
	
	public String TRACKING_NUMBER;
	public String UNMOVED_ID; 
	public int TOGO_MS;
}
