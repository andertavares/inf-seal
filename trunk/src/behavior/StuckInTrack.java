package behavior;

import java.util.Vector;

import sealbot.Action;
import sealbot.Car;
import sealbot.SensorModel;


public class StuckInTrack extends Behavior {
	
	private int stuckFor;
	
	private boolean isStuck;
	
	private final int THRESHOLD = 100;
	
	Action theAction;
	private double minCenterDist;
	
	public StuckInTrack(){
		stuckFor = 0;
		minCenterDist = 0;
		theAction = new Action();
	}

	public double score(SensorModel sensors, Vector<Car> opponentData) {
		
		minCenterDist = minCenterTrackSensor(sensors);
		
		 if (isStuck) return 1;
		 if (stuckFor >= THRESHOLD) {
            isStuck = true;
            stuckFor = THRESHOLD;
            return 1;
		 }
		 else if (sensors.getSpeed() < 10 && minCenterTrackSensor(sensors) < 3)
            stuckFor++;
		 else
	        stuckFor = 0;
		 
		 return -1;
		 /*
		//armazena o ultimo valor
		centerPotential = centerPotential(sensors);
		
		//testa o potencial, se estiver muito proximo, sinaliza como preso
		if (centerPotential > 0.8) {
			isStuck = true;
			return 1;
		}
		//se esta preso, espera afastar-se para dizer que nao esta mais preso
		if(isStuck && centerPotential > 0.2) return 1;
		
		isStuck = false;
		return -1;*/
	}

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
		return "Stuck in track. " + minCenterDist  + ", " + stuckFor;/*. stuckFor=" + stuckFor + ", gear = " + theAction.gear + ", accel= " 
		+ theAction.accelerate + ", steer=" + theAction.steering;*/
	}
	
	/**
	 * Retorna o potencial detectado pelos sensores 'a esquerda
	 * @param sensors
	 * @return
	 */
	private double leftPotential(SensorModel sensors) {
		double min = 100;
		for (int i = 0; i < 6; i++) {
			if (sensors.getTrackEdgeSensors()[i] < min) min = sensors.getTrackEdgeSensors()[i];
		}
		return (1/(Math.sqrt(min)))/1.55;
	}
	
	/**
	 * Retorna o potencial detectado pelo sensor central
	 * @param sensors
	 * @return
	 */
	private double minCenterTrackSensor(SensorModel sensors) {
		
		double minCenterValue = 10000;
		
		for(int i = 8; i < 11; i++){
			//System.out.println("Sensor["+i+"]" + " " + sensors.getTrackEdgeSensors()[i]);
			if (sensors.getTrackEdgeSensors()[i] < minCenterValue) 
				minCenterValue = sensors.getTrackEdgeSensors()[i];
		}
		
		return minCenterValue;
	}
	
	/**
	 * Retorna o potencial detectado pelo sensor 'a direita
	 * @param sensors
	 * @return
	 */
	private double rightPotential(SensorModel sensors) {
		double min = 100;
		for (int i = 13; i < 19; i++) {
			if (sensors.getTrackEdgeSensors()[i] < min) min = sensors.getTrackEdgeSensors()[i];
		}
		return (1/(Math.sqrt(min)))/1.55;
	}

}
