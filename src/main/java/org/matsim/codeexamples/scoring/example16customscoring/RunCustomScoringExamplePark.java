package org.matsim.codeexamples.scoring.example16customscoring;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.scoring.ScoringFunction;
import org.matsim.core.scoring.ScoringFunctionFactory;
import org.matsim.core.scoring.SumScoringFunction;
import org.matsim.core.scoring.functions.CharyparNagelActivityScoring;
import org.matsim.core.scoring.functions.CharyparNagelAgentStuckScoring;
import org.matsim.core.scoring.functions.CharyparNagelLegScoring;
import org.matsim.core.scoring.functions.CharyparNagelMoneyScoring;
import org.matsim.core.scoring.functions.ScoringParameters;

public class RunCustomScoringExamplePark {

	public static final class ParkScoringFunctionFactory implements ScoringFunctionFactory{
		private final Scenario scenario;
		//private final ObjectAttributes personAttributes;
		
		public ParkScoringFunctionFactory(Scenario scenario){
			this.scenario = scenario;
			//this.personAttributes = personAttributes;
		}
		
		@Override
		public ScoringFunction createNewScoringFunction(Person person) {
			SumScoringFunction sumScoringFunction = new SumScoringFunction();

			// Score activities, legs, payments and being stuck
			// with the default MATSim scoring based on utility parameters in the config file.
			final ScoringParameters params = new ScoringParameters.Builder(scenario, person.getId()).build();
			sumScoringFunction.addScoringFunction(new CharyparNagelActivityScoring(params));
			sumScoringFunction.addScoringFunction(new CharyparNagelLegScoring(params, scenario.getNetwork()));
			sumScoringFunction.addScoringFunction(new CharyparNagelMoneyScoring(params));
			sumScoringFunction.addScoringFunction(new CharyparNagelAgentStuckScoring(params));

			sumScoringFunction.addScoringFunction(new ParkScoring()); 

			return sumScoringFunction;
		}
	}
}
