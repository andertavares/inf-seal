package behavior;

import java.util.Vector;

import sealbot.Action;
import sealbot.Car;
import sealbot.SensorModel;

public class LookingForOpponents extends Behavior {

	@Override
	public double score(SensorModel sensors, Vector<Car> opponentData) {
		// TODO Auto-generated method stub
		
		
		return 0;
	}

	@Override
	public Action control(SensorModel sensors) {
		Action a = new Action();
		
		//angle = sensor['minOpponentsAngle'];
		
		a.accelerate = 1;
        a.steering = getSteer(sensors);
		
		return a;
	}
	
}
