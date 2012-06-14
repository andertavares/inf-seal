package behavior;

import sealbot.Action;
import sealbot.SensorModel;

public abstract class Behavior {
		
	
	
	abstract public double score(SensorModel sensors);
	abstract public Action control(SensorModel sensors);
	
}
