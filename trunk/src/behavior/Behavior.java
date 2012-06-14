package behavior;

import java.util.Vector;

import sealbot.Action;
import sealbot.SensorModel;
import sealbot.Car;

public abstract class Behavior {
	
	abstract public double score(SensorModel sensors, Vector<Car> opponentData);
	abstract public Action control(SensorModel sensors);
	
}
