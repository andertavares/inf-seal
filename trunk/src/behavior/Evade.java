package behavior;

import java.util.Vector;

import score.AttackScore;
import score.EvadeScore;
import sealbot.Action;
import sealbot.Car;
import sealbot.SensorModel;

public class Evade extends Behavior {

	EvadeScore es;
	
	private final double LIFE_THRESHOLD = 6000;
	
	public Evade(){
		es = new EvadeScore();
	}
	@Override
	public double score(SensorModel sensors, Vector<Car> opponentData) {
		double oppScore = -1;
		highestScore = null;
		
		for(Car c : opponentData){
			double currentScore = es.getScore(c.getAngle(), c.getDistance(), c.getDirection());
			if(currentScore > oppScore){
				oppScore = currentScore;
				highestScore = c;		
			}
		}
		
		if(sensors.getDamage() > LIFE_THRESHOLD && oppScore > 0) return 1;
		
		return oppScore;
	}

	@Override
	public Action control(SensorModel sensors) {
		/*Action action = new Action();
        action.accelerate = 1;
        action.steering = highestScore.getAngle() / Math.PI;

        return action;*/
		Action a = new Action();
        a.accelerate = 1;
        a.steering = getSteer(sensors);
		
		return a;
	}
	
	public String toString(){
		return "EVD";
	}

}
