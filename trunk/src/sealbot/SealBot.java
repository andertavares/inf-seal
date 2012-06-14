package sealbot;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;



//import sealbot.Car;

import behavior.Behavior;

public class SealBot extends Controller {
	List<Behavior> behaviorList;
	private Vector<Car> opponentData;
	
	public SealBot(){
		behaviorList = new ArrayList<Behavior>();
		//TODO: adicionar no behaviorList um item pra cada comportamento
	}

	@Override
	public Action control(SensorModel sensors) {
		
		//atualiza a situacao dos oponentes dadas as novas leituras dos sensores
		updateOpponents(sensors.getOpponentSensors());
		
		//procura o melhor comportamento (de maior score) na lista
		Behavior bestBehavior = null;
		double bestScore = Double.NEGATIVE_INFINITY;
		
		for(Behavior b : behaviorList){
			double currentScore = b.score(sensors, opponentData); 
			if( currentScore > bestScore) {
				bestBehavior = b;
				bestScore = currentScore;
			}
		}
		
		return bestBehavior.control(sensors);
	}
	
	/** 
	 * METODO DO COBOSTAR
	 * Updates the opponents that are currently perceived around the car. 
	 * 
	 * @param opponents The opponents distances as double.
	 */
	@SuppressWarnings("unchecked")
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

}
