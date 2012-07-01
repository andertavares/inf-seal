package behavior;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Vector;

import score.AttackScore;
import sealbot.Action;
import sealbot.Car;
import sealbot.SensorModel;

public class Attack extends Behavior {
	
	AttackScore as;
	Vector<Double> Vx, Vy;
	int size = 5;
	
	public Attack(){
		as = new AttackScore();
		Vx = new Vector<Double>();
		Vy = new Vector<Double>();
		
		for (int i = 0; i < size; i++) {
			Vx.add(0.);
			Vy.add(0.);
		}
		Vx.setSize(size);
		Vy.setSize(size);
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
            
            //angle = sensor['minOpponentsAngle']
            Action action = new Action();
//            double C1,C2,d,V,Vx,Vy,Vop,alpha,gamma,teta,Ta;
//            double Vr[] = new double[2];
//            double Sr[] = new double[2];
//            double Sf[] = new double[2];
//            
//            alpha = Math.toDegrees(highestScore.getAngle());
//            gamma = 90 - alpha;
//            V = sensors.getSpeed();
//            d = highestScore.getDistance();
//            
//            Vop = highestScore.getVelocity(); sensors.
//            teta = Math.toDegrees(highestScore.getDirection());
//            Vx = Vop*Math.sin(teta);
//            Vy = Vop*Math.cos(teta);
//
//            Vr[0] = Vx;
//            Vr[1] = Vy - V;
//            
//            C1 = Math.cos(gamma)*d;
//            C2 = Math.sin(gamma)*d;
//            
//            Sr[0] = C1;
//            Sr[1] = C2;
//            
//            Ta = Math.sqrt(Math.pow(Sr[0], 2) + Math.pow(Sr[1],2)) / 
//                    Math.sqrt(Math.pow(Vr[0], 2) + Math.pow(Vr[1],2));
//            
//            Sf[0] = C1 + (Vx*Ta);
//            Sf[1] = C2 + (Vy*Ta);
//            
//            DecimalFormat df = new DecimalFormat("0.##");
            //System.out.println(df.format(Vop) + "\t" + df.format(V));
            //System.out.println(df.format(Sf) + "\t" + df.format(Ta));
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
            
            //vr_x = highestScore.getVelocity();
            
            double vr = highestScore.getRelativeVelocity() == 0.0 ? 1 : highestScore.getRelativeVelocity();
            approachTime = Math.abs( highestScore.getDistance() / vr );
            
            if(approachTime < 0) {
            	approachTime = highestScore.getDistance() / 10;
            	System.out.print("a");
            }
            
            if(approachTime > 20){
            	approachTime = 20;
            	System.out.print("d");
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
			//if (Math.abs(alphaf) < 0.1) alphaf = 0.;
            
            //System.out.println(alphaf + "\t" + alpha + "\t" + t);
            
			action.steering = -alphaf;
//            if (alphaf > 0) action.steering = - 1;
//            else            action.steering = 1;
//            
            action.accelerate = 0.8;
			if (Math.abs(alphaf) > 0.6 && sensors.getSpeed() > 50) action.accelerate = 0.2;
			if (Math.abs(alphaf) < 0.2) action.accelerate = 1.;
//            
//            if (Math.abs(alphaf) < 0.2) {
//            	action.steering = -alphaf/5;
//            	action.accelerate = 1;
//            }
//            else if (Math.abs(alphaf) > 0.6 && sensors.getSpeed() > 50) {
//            	action.accelerate = 0;
//            }
            /*
            System.out.printf(
        		"%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\n", 
        		alphaf,alpha,t,action.steering,action.accelerate,sensors.getSpeed()
        	);
            */
            /*System.out.println(
        		alphaf + "\t" + alpha + "\t" + t + "\t" + action.steering
        		+ "\t" + action.accelerate + "\t" + sensors.getSpeed()
        	);
            
            System.out.printf(
        		"%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\n", 
        		alphaf,alpha,approachTime,posx,posy,pfx,pfy,vx,vy,
        		highestScore.getDirection(),highestScore.getAngle()
        	);*/
            
//            System.out.printf(
//        		"%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\n", 
//        		alphaf,alpha,approachTime,vx,vy,vr,highestScore.getDistance(),highestScore.getDirection(),highestScore.getAngle()
//        		,action.accelerate,action.steering
//        	);
            
            
            
            return action;
		
		/*
		Vector u;
		Bool left = false;Bool right = false;
		//Vector Vr, Sr, St; velocidade relativa, posicao relativa, 
		
		double tcVr = highestScore.getRelativeVelocity();// Prey.vVelocity - Predator.vVelocity;
		double dist = highestScore.getDistance(); //Sr = Prey.vPosition - Predator.vPosition;
		
		double approachTime = dist / tcVr;		//Ta = Sr.Magnitude() / Vr.Magnitude();
				
		Sf = Prey.vPosition + (Prey.vVelocity * Ta);
		u = VRotate2D(-Predator.fOrientation,(Sf - Predator.vPosition));
		//daqui em diante, permanece o mesmo código do algoritmo anterior
		u.Normalize();if (u.x < -_TOL)
			left = true;
		else if (u.x > _TOL)
				right = true;
		Predator.SetThrusters(left, right); //ajusta a direção
		*/
		
	}
	
	public String toString(){
		return "Attack";
	}

}
