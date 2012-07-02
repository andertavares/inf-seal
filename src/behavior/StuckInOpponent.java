package behavior;

import java.util.Date;
import java.util.Vector;

import sealbot.Action;
import sealbot.Car;
import sealbot.SensorModel;

public class StuckInOpponent extends Behavior {

	private long lastDet = 0;
	boolean latCol = false;
	boolean frontCol = false;
	
	@Override
	public double score(SensorModel sensors, Vector<Car> opponentData) {

		double s[] = sensors.getOpponentSensors();
		double frontMin = 300, latMin = 300, ret = -1;
		long acTime = 0;
		frontCol = false;
		latCol = false;
		
		for (int i = 0; i < 12; i++) {
			if (latMin > s[i])
				latMin = s[i];
		}
		for (int i = 24; i < 36; i++) {
			if (latMin > s[i])
				latMin = s[i];
		}
		for (int i = 12; i < 24; i++) {
			if (frontMin > s[i])
				frontMin = s[i];
		}
		
		// Preso pela lateral ou oponente atacando por tras
		if (sensors.getSpeed() < 5 && latMin < 5) latCol = true;
		if (latCol) {
			acTime = (new Date()).getTime();
			if (lastDet == 0) {
				lastDet = acTime;
				ret = -1;
			}
			else if (acTime - lastDet > 500) {
				ret = 0.95;
			}
		}
		
		// Preso pela frente
		if (sensors.getSpeed() < 5 && frontMin < 5) frontCol = true;
		if (frontCol) {
			acTime = (new Date()).getTime();
			if (lastDet == 0) {
				lastDet = acTime;
				ret = -1;
			}
			else if (acTime - lastDet > 500) {
				ret = 0.95;
			}
		}
		
		if (!latCol && !frontCol)
			lastDet = 0;

		//System.out.println(ret);

		return ret;
                
	}

	@Override	
	public Action control(SensorModel sensors) {
		// TODO Auto-generated method stub
		
		if (latCol) System.out.println("Lateral stuck");
		if (frontCol) System.out.println("Front stuck");
		
		Action a;
		if (frontCol) {
			a = new Action();
			a.gear = -1;
			a.accelerate = 1;
		}
		else {
			a = (new Evade()).control(sensors);
		}
		return a;
	}
	
	public String toString() {
		return "SO";
	}

}
