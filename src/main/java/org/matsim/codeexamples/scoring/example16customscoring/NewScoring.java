package org.matsim.codeexamples.scoring.example16customscoring;

import org.matsim.api.core.v01.events.Event;
import org.matsim.core.scoring.SumScoringFunction;

final class NewScoring implements SumScoringFunction.ArbitraryEventScoring{

	private double score;
	int test = 0;
	
	@Override
	public void handleEvent(Event event) {
		if (event instanceof PersonLeavesTaxiEvent) {
			score -= 5.80*0.0616752;
			//System.out.println(score);
		}else if (event instanceof PersonLeavesFHVEvent) {
			score -= 5.25*0.0616752;
			//System.out.println(score);
		}else if(event instanceof PersonParkingEvent) {
			score -= 5.19*0.0616752;
			//System.out.println(score);
		}else if (event instanceof TollPersonEvent1){
			if(event.getTime()<6*60*60 ||(event.getTime()<16*60*60 && event.getTime()>=10*60*60 )||event.getTime()>=20*60*60 ){
				if(test == 0)
					score -= 10.5*0.0616752;
				else
					score -= 10.5*100;
				//System.out.println("toll:"+score);
				//System.out.println(event.getlinkID().tostring());
			}else{
				if(test == 0)
					score -= 12.5*0.0616752;
				else
					score -= 10.5*100;
				//System.out.println("toll:"+score);
			}
		}else if (event instanceof TollPersonEvent2){
			if(test == 0)
				score -= 6.12*0.0616752;
			else
				score -= 10.5*100;
		}
			
	}
	

	@Override public void finish() {}

	@Override
	public double getScore() {
		return score;
	}
}

