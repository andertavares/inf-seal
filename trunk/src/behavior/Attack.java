package behavior;

import java.util.Vector;

import sealbot.Action;
import sealbot.Car;
import sealbot.SensorModel;

public class Attack extends Behavior {
	
	

	@Override
	public double score(SensorModel sensors, Vector<Car> opponentData) {
		// TODO Auto-generated method stub
		
		highestScore = opponentData.firstElement();
		return 1;
	}

	@Override
	public Action control(SensorModel sensors) {
		
		//angle = sensor['minOpponentsAngle']
		Action action = new Action();
        action.accelerate = 1;
        action.steering = - highestScore.getAngle() / Math.PI;

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

}
