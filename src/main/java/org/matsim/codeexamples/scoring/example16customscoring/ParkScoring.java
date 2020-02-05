package org.matsim.codeexamples.scoring.example16customscoring;

import org.matsim.api.core.v01.events.Event;
import org.matsim.core.scoring.SumScoringFunction;

final class ParkScoring implements SumScoringFunction.ArbitraryEventScoring{
	
	private double score;
	
	@Override
	public void handleEvent(Event event) {
		if (event instanceof PersonParkingEvent) {
			//score -= 5.19*0.0616752;
			score -= 5*100;
		}
	}

	@Override public void finish() {}

	@Override
	public double getScore() {
		return score;
	}
	

}