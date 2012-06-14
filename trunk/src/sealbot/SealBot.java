package sealbot;

import java.util.ArrayList;
import java.util.List;

import behavior.Behavior;

public class SealBot extends Controller {
	List<Behavior> behaviorList;
	
	public SealBot(){
		behaviorList = new ArrayList<Behavior>();
		//TODO: adicionar no behaviorList um item pra cada comportamento
	}

	@Override
	public Action control(SensorModel sensors) {
		
		//procura o melhor comportamento (de maior score) na lista
		Behavior bestBehavior = null;
		double bestScore = Double.NEGATIVE_INFINITY;
		
		for(Behavior b : behaviorList){
			double currentScore = b.score(sensors); 
			if( currentScore > bestScore) {
				bestBehavior = b;
				bestScore = currentScore;
			}
		}
		
		return bestBehavior.control(sensors);
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
