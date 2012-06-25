package behavior;

import java.util.Vector;

import sealbot.Action;
import sealbot.Car;
import sealbot.SensorModel;

public class LookingForOpponents extends Behavior {

	final float steerLock=(float) 0.785398;
	
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
	
	private float getSteer(SensorModel sensors){
		// steering angle is compute by correcting the actual car angle w.r.t. to track 
		// axis [sensors.getAngle()] and to adjust car position w.r.t to middle of track [sensors.getTrackPos()*0.5]
	    float targetAngle=(float) (sensors.getAngleToTrackAxis()-sensors.getTrackPosition()*0.5);
	    // at high speed reduce the steering command to avoid loosing the control
	    /*if (sensors.getSpeed() > steerSensitivityOffset)
	        return (float) (targetAngle/(steerLock*(sensors.getSpeed()-steerSensitivityOffset)*wheelSensitivityCoeff));
	    else*/
        return (targetAngle)/steerLock;

	}

}
