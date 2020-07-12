package guru.bonacci.timesup.home.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trace {
	
	public String tracking_number;
	public String unmoved_id; 
	public int togo_ms;
}
