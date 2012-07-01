package behavior;

import java.util.Date;
import java.util.Vector;

import sealbot.Action;
import sealbot.Car;
import sealbot.SensorModel;

public class StuckInOpponent extends Behavior {

	private long lastDet = 0;
	
	@Override
	public double score(SensorModel sensors, Vector<Car> opponentData) {

		double s[] = sensors.getOpponentSensors();
		double sd = 0, ret = -1;
		long acTime = 0;
		boolean cret = false;

		for (int i = 0; i < 12; i++) sd += (300 - s[i]);
		for (int i = 24; i < 36; i++) sd += (300 - s[i]);

		if (sensors.getSpeed() < 5 && sd > 295) cret = true;
		if (cret) {
			acTime = (new Date()).getTime();
			if (lastDet == 0) {
				lastDet = acTime;
				ret = -1;
			}
			else if (acTime - lastDet > 500) {
				ret = 0.95;
			}
		}
		else
			lastDet = 0;
		
		//System.out.println(ret);

		return ret;
                
	}

	@Override
	public Action control(SensorModel sensors) {
		// TODO Auto-generated method stub
		Action a = (new Evade()).control(sensors);
		return a;
	}
	
	public String toString() {
		return "Stuck in Opponent";
	}

}
