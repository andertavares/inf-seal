package behavior;

import java.util.Vector;

import sealbot.Action;
import sealbot.Car;
import sealbot.SensorModel;

public class Stuck extends Behavior {
	
	private int stuckFor;
	
	private boolean isStuck;
	
	private final int THRESHOLD = 100;
	
	Action theAction;
	
	public Stuck(){
		stuckFor = 0;
		theAction = new Action();
	}

	@Override
	public double score(SensorModel sensors, Vector<Car> opponentData) {
		
		 if (isStuck) return 1;
		 if (stuckFor >= THRESHOLD) {
            isStuck = true;
            stuckFor = THRESHOLD;
            return 1;
		 }
		 else if (sensors.getSpeed() < 10)
            stuckFor++;
		 else
	        stuckFor = 0;
		 
		 return -1;
	}

	@Override
	public Action control(SensorModel sensors) {
		//Action action = new Action();
		theAction.gear = -1;
        theAction.accelerate = 1;
        theAction.steering = - getSteer(sensors);
        
        stuckFor--;
        if (stuckFor <= 0){
            isStuck = false;
        }
        
        //System.out.println("gear" + sensors.getGear());
        
        return theAction;
	}
	
	public String toString(){
		return "Stuck. stuckFor=" + stuckFor + ", gear = " + theAction.gear + ", accel= " 
		+ theAction.accelerate + ", steer=" + theAction.steering;
	}

}
