package behavior;

import java.util.Vector;

import sealbot.Action;
import sealbot.SensorModel;
import sealbot.Car;

public abstract class Behavior {
	
	protected Car highestScore;
	
	abstract public double score(SensorModel sensors, Vector<Car> opponentData);
	abstract public Action control(SensorModel sensors);
	
	protected final float steerLock = (float) 0.785398;
	
	protected float getSteer(SensorModel sensors){
		// steering angle is compute by correcting the actual car angle w.r.t. to track 
		// axis [sensors.getAngle()] and to adjust car position w.r.t to middle of track [sensors.getTrackPos()*0.5]
		float targetAngle;
		double ro = sensors.getAngleToTrackAxis();
		
		if (Math.abs(ro) > Math.PI/2) { // lado inverso
			double ang = sensors.getAngleToTrackAxis();
			if (ang > 0)	ang = ang - Math.PI;
			else			ang = ang + Math.PI;
			targetAngle=(float) (ang + sensors.getTrackPosition()*0.5);
		} else {
			targetAngle=(float) (sensors.getAngleToTrackAxis()-sensors.getTrackPosition()*0.5);
		}
	    // at high speed reduce the steering command to avoid loosing the control
	    /*if (sensors.getSpeed() > steerSensitivityOffset)
	        return (float) (targetAngle/(steerLock*(sensors.getSpeed()-steerSensitivityOffset)*wheelSensitivityCoeff));
	    else*/
        return (targetAngle)/steerLock;

	}
	
}
