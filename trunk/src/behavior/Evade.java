package behavior;

import java.util.Vector;

import score.AttackScore;
import score.EvadeScore;
import sealbot.Action;
import sealbot.Car;
import sealbot.SensorModel;

public class Evade extends Behavior {

	EvadeScore es;
	
	public Evade(){
		es = new EvadeScore();
	}
	@Override
	public double score(SensorModel sensors, Vector<Car> opponentData) {
		double maxScore = -1;
		highestScore = null;
		
		for(Car c : opponentData){
			double currentScore = es.getScore(c.getAngle(), c.getDistance(), c.getDirection());
			if(currentScore > maxScore){
				maxScore = currentScore;
				highestScore = c;		
			}
		}
		
		return maxScore;
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
		return "Evade";
	}

}
