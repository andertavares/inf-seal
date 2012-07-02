package behavior;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import score.AttackScore;
import score.SideAttackScore;
import sealbot.Action;
import sealbot.Car;
import sealbot.SensorModel;

public class SideAttack extends Behavior {
	
	SideAttackScore as;
	Vector<Double> Vx, Vy, Alphaf;
	int size = 5;
	
	public SideAttack(){
		as = new SideAttackScore();
		Vx = new Vector<Double>();
		Vy = new Vector<Double>();
		Alphaf = new Vector<Double>();
		
		for (int i = 0; i < size; i++) {
			Vx.add(0.);
			Vy.add(0.);
			Alphaf.add(0.);
			
		}
		Vx.setSize(size);
		Vy.setSize(size);
		Alphaf.setSize(size);
	}
	
	@Override
	public double score(SensorModel sensors, Vector<Car> opponentData) {
		double maxScore = -1;
		highestScore = null;
		
		for(Car c : opponentData){
			double currentScore = as.getScore(c.getAngle(), c.getDistance(), c.getDirection());
			if(currentScore > maxScore){
				maxScore = currentScore;
				highestScore = c;		
			}
		}
		return maxScore;
	}

	@Override
	public Action control(SensorModel sensors) {
            
            Action action = new Action();
            
            if(highestScore.getAngle() < 0) action.steering = 1;
            else action.steering = -1;
            
            if(sensors.getSpeed() > 50) action.accelerate = 0.2;
            else action.accelerate = 1;
            
            return action;
		
		
	}
	
	public String toString(){
		return "SATK";
	}

}
