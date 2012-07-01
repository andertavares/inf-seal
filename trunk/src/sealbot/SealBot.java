package sealbot;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;



//import sealbot.Car;

import behavior.Attack;
import behavior.Behavior;
import behavior.Evade;
import behavior.LookingForOpponents;
import behavior.StuckInTrack;

/**
 * The SealBot
 * @author Anderson, Gabriel, Renato and Sergio
 *
 * Implements the Singleton design pattern
 */
public class SealBot extends Controller {
	private List<Behavior> behaviorList;
	private Vector<Car> opponentData;
	private SensorModel mySensors;
	
	int printFrequency;
	
	final int[]  gearUp = {8500,9000,9500,9500,9500,0}; //Renato
	final int[]  gearDown = {0,3300,6200,7000,7300,7700};
	
	//final int[]  gearUp={9500,9500,9500,10000,10000,0}; //Gabriel
	//final int[]  gearDown={0,2500,3000,3000,3500,3500};
	
	//private static final int[]  gearUp = {9500, 9400, 9500, 9500, 9500, 0}; //Sergio/Anderson
	//private static final int[]  gearDown =  {0, 3300, 6200, 7000, 7300, 7700};

	
	/* Clutching Constants */
	final float clutchMax	= (float) 0.5;
	final float clutchDelta	=(float) 0.05;
	final float clutchRange	=(float) 0.82;
	final float	clutchDeltaTime = (float) 0.02;
	final float clutchDeltaRaced = 10;
	final float clutchDec	=(float) 0.01;
	final float clutchMaxModifier=(float) 1.3;
	final float clutchMaxTime=(float) 1.5;
	
	public SealBot(){
		behaviorList = new ArrayList<Behavior>();
		opponentData = new Vector<Car>();
		
		behaviorList.add(new LookingForOpponents());
		behaviorList.add(new Attack());
		behaviorList.add(new Evade());
		behaviorList.add(new StuckInTrack());
		
		printFrequency = 0;
	}

	@Override
	public Action control(SensorModel sensors) {
		
		mySensors = sensors; //update my sensors with the new readings
		
		//updates opponent status given new sensor readings
		updateOpponents(sensors.getOpponentSensors());
		
		//searches for the best behavior (highest score) on the list
		Behavior bestBehavior = null;
		double bestScore = Double.NEGATIVE_INFINITY;
		
		for(Behavior b : behaviorList){
			double currentScore = b.score(sensors, opponentData); 
			if( currentScore > bestScore) {
				bestBehavior = b;
				bestScore = currentScore;
			}
		}
		if (bestBehavior == null) {
			System.out.println("Warning: no best behavior was found");
			return new Action();
		}
		Action a = bestBehavior.control(sensors);
		//System.out.println("behavior: " + bestBehavior );
		
		printFrequency++;
		if (printFrequency > 10){
			System.out.println("Behavior: " + bestBehavior);
			System.out.println(
				"SEAL dmg: " + sensors.getDamage() + " / Enemy dmg: " + sensors.getOtherdamage()
			);
			printFrequency = 0;
		}
		
		if(! (bestBehavior instanceof StuckInTrack))
			a.gear = getGear(sensors);
		
		return a;
	}
	
	float clutching(SensorModel sensors, float clutch)
	{
	  	 
	  float maxClutch = clutchMax;

	  // Check if the current situation is the race start
	  if (sensors.getCurrentLapTime()<clutchDeltaTime  && getStage()==Stage.RACE && sensors.getDistanceRaced()<clutchDeltaRaced)
	    clutch = maxClutch;

	  // Adjust the current value of the clutch
	  if(clutch > 0)
	  {
	    double delta = clutchDelta;
	    if (sensors.getGear() < 2)
		{
	      // Apply a stronger clutch output when the gear is one and the race is just started
		  delta /= 2;
	      maxClutch *= clutchMaxModifier;
	      if (sensors.getCurrentLapTime() < clutchMaxTime)
	        clutch = maxClutch;
		}

	    // check clutch is not bigger than maximum values
		clutch = Math.min(maxClutch,clutch);

		// if clutch is not at max value decrease it quite quickly
		if (clutch!=maxClutch)
		{
		  clutch -= delta;
		  clutch = Math.max((float) 0.0,clutch);
		}
		// if clutch is at max value decrease it very slowly
		else
			clutch -= clutchDec;
	  }
	  return clutch;
	}
	
	/**
	 * Returns the car vector containing the opponnent data
	 * @return
	 */
	public Vector<Car> getOpponentData(){
		return opponentData;
	}
	
	/**
	 * Returns the current sensor readings
	 * @return
	 */
	public SensorModel getSensors(){
		return mySensors;
	}
	
	
	private int getGear(SensorModel sensors){
	    int gear = sensors.getGear();
	    double rpm  = sensors.getRPM();

	    // if gear is 0 (N) or -1 (R) just return 1 
	    if (gear<1)
	        return 1;
	    // check if the RPM value of car is greater than the one suggested 
	    // to shift up the gear from the current one     
	    if (gear <6 && rpm >= gearUp[gear-1])
	        return gear + 1;
	    else
	    	// check if the RPM value of car is lower than the one suggested 
	    	// to shift down the gear from the current one
	        if (gear > 1 && rpm <= gearDown[gear-1])
	            return gear - 1;
	        else // otherwhise keep current gear
	            return gear;
	}
	
	/** 
	 * METODO DO COBOSTAR
	 * Updates the opponents that are currently perceived around the car. 
	 * 
	 * @param opponents The opponents distances as double.
	 */
	public void updateOpponents(double[] opponents)
	{
		Vector<Car> previousOpponents = (Vector<Car>)opponentData.clone();
		// first add all currently viewable cars to vector currentOpponents
		Vector<Car> currentOpponents = new Vector<Car>();
		for (int i=0; i<36; i++)
		{
			if (opponents[i] < 300)//ML
			{
				currentOpponents.add(new Car(i, opponents[i]));
			}
		}

		// now compare currently viewable cars with the cars in the history data
		while (currentOpponents.size() > 0 && previousOpponents.size() > 0)
		{
			// opponents from last iteration (best matching one to currently seens ones)
			Car bestPrevOppMatching = previousOpponents.firstElement();
			// currently seen opponent
			Car bestViewableOppMatching = currentOpponents.firstElement();

			double minDist = bestPrevOppMatching.distanceToExpetation(bestViewableOppMatching);

			for (Car prevOpponent : previousOpponents)
			{
				if (prevOpponent.distanceToExpetation(bestViewableOppMatching) < minDist)
				{
					bestPrevOppMatching = prevOpponent;
					minDist = prevOpponent.distanceToExpetation(bestViewableOppMatching);
				}
			}

			// bestPrevOppMatching is now the closest previous opponent to the
			// viewable "bestViewableOppMatching" opponent with a 
			// distance of "minDist"

			// now check the other way round... 
			double minDistOld;
			do
			{
				minDistOld = minDist;
				// if there is another viewable opponent that is even closer to the 
				// selected previous one... then change things... 
				for (Car currOpponent : currentOpponents)
				{
					if (bestPrevOppMatching.distanceToExpetation(currOpponent) < minDist)
					{
						bestViewableOppMatching = currOpponent;
						minDist = bestPrevOppMatching.distanceToExpetation(currOpponent);
					}
				}

				for (Car prevOpponent : previousOpponents)
				{
					if (prevOpponent.distanceToExpetation(bestViewableOppMatching) < minDist)
					{
						bestPrevOppMatching = prevOpponent;
						minDist = prevOpponent.distanceToExpetation(bestViewableOppMatching);
					}
				}
			}
			while (minDist < minDistOld);

			bestPrevOppMatching.update(bestViewableOppMatching);

			currentOpponents.remove(bestViewableOppMatching);
			previousOpponents.remove(bestPrevOppMatching);
		}

		for (Car newlyViewableOpponent : currentOpponents)
		{
			opponentData.add(newlyViewableOpponent);
		}

		for (Car notViewableOpponent : previousOpponents)
		{
			notViewableOpponent.setUnseen(true);
		}
	}

	@Override
	public void reset() {
		opponentData.clear();
		System.out.println("SealBot restarting.");
	}

	@Override
	public void shutdown() {
		System.out.println("SealBot shutting down.");
	}

}
