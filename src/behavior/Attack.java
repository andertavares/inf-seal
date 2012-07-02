package behavior;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import score.AttackScore;
import sealbot.Action;
import sealbot.Car;
import sealbot.SensorModel;

public class Attack extends Behavior {
	
	AttackScore as;
	Vector<Double> Vx, Vy, Alphaf;
	int size = 5;
	
	public Attack(){
		as = new AttackScore();
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

            double dist,alpha,vel,posx,posy,posxf,posyf,dirx,diry,t,dir,alphaf;
            dist = highestScore.getDistance();
            dir = highestScore.getDirection();
            alpha = highestScore.getAngle();
            
            posy = Math.cos(alpha)*dist;
            posx = Math.sin(alpha)*dist;
            
            dirx = Math.sin(dir);
            diry = -Math.cos(dir);
            
            vel = highestScore.getRelativeVelocity();
            
            t = dist / 10;
            
            posxf = posx + dirx * vel * t;
            posyf = posy + diry * vel * t;
            
            alphaf = Math.atan2(posyf, posxf);
            
            //NOVA TENTATIVA DE CALCULO DO ALPHAF
            double v, vx, vy, pfx, pfy, dx, dy, approachTime;
            
            double vr = highestScore.getRelativeVelocity() == 0.0 ? 1 : highestScore.getRelativeVelocity();
            approachTime = Math.abs( highestScore.getDistance() / vr );
            
            if(approachTime < 0) {
            	approachTime = highestScore.getDistance() / 10;
            }
            
            if(approachTime > 20){
            	approachTime = 20;
            }
            
            v = highestScore.getVelocity();
            vx = v * Math.sin(2 * Math.PI - highestScore.getDirection());
            vy = v * Math.cos(2 * Math.PI - highestScore.getDirection());
			Vx.add(vx); Vx.removeElementAt(0);
			Vy.add(vy); Vy.removeElementAt(0);
			Vector<Double> Vxc = (Vector<Double>) Vx.clone();
			Vector<Double> Vyc = (Vector<Double>) Vy.clone();
			Collections.sort(Vxc);
			Collections.sort(Vyc);
			vx = Vxc.get(2);
			vy = Vyc.get(2);
            
            pfx = posx + vx * approachTime;
            pfy = posy + vy * approachTime;
            
            alphaf = Math.atan2(pfx, pfy);
            Alphaf.add(alphaf); Alphaf.removeElementAt(0);
            
            alphaf = alpha;//mean(Alphaf);
            
			action.steering = -alphaf;//;
            action.accelerate = 0.8;
            
			if (Math.abs(alphaf) > 0.6 && sensors.getSpeed() > 50) {
				action.accelerate = 0; //makes a sharp turn
			}
			if (Math.abs(alphaf) < 0.2) {
				action.accelerate = 1.;
			}
            return action;
		
		
	}
	
	private double mean(List<Double> l){
		double acc = 0;
		for (double d : l){
			acc += d;
		}
		return acc / l.size();
	}
	
	public String toString(){
		return "ATK";
	}

}
