package sealbot;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;



//import sealbot.Car;

import behavior.Behavior;

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
	
	
	
	public SealBot(){
		behaviorList = new ArrayList<Behavior>();
		opponentData = new Vector<Car>();
		//TODO: adicionar no behaviorList um item pra cada comportamento
		
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
		return bestBehavior.control(sensors);
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
