package org.matsim.codeexamples.scoring.example16customscoring;

import java.util.HashMap;
import java.util.Map;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.PersonEntersVehicleEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.events.handler.PersonEntersVehicleEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.scoring.ScoringFunction;
import org.matsim.core.scoring.ScoringFunctionFactory;
import org.matsim.core.scoring.SumScoringFunction;
import org.matsim.core.scoring.functions.CharyparNagelActivityScoring;
import org.matsim.core.scoring.functions.CharyparNagelAgentStuckScoring;
import org.matsim.core.scoring.functions.CharyparNagelLegScoring;
import org.matsim.core.scoring.functions.CharyparNagelMoneyScoring;
import org.matsim.core.scoring.functions.ScoringParameters;
import org.matsim.utils.objectattributes.ObjectAttributes;
import org.matsim.vehicles.Vehicle;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;

import javax.inject.Inject;


public class RunTestExample {

	static final String DISLIKES_LEAVING_EARLY_AND_COMING_HOME_LATE = "DISLIKES_LEAVING_EARLY_AND_COMING_HOME_LATE";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String configFile = "scenarios/equil/example5-config.xml" ;
		
		Config config = ConfigUtils.loadConfig(configFile);
		
		config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);
		
		final Scenario scenario = ScenarioUtils.loadScenario(config);

		// Every second person gets a special property which influences their score.
		// ObjectAttributes can be written to and read from files, so in reality,
		// this would come from a census, a pre-processing step, or from anywhere else, 
		// but for this example I just make it up.
		final ObjectAttributes personAttributes = scenario.getPopulation().getPersonAttributes();
		for (Person person : scenario.getPopulation().getPersons().values()) {
			if (Integer.parseInt(person.getId().toString()) % 2 == 0) {
				personAttributes.putAttribute(person.getId().toString(), DISLIKES_LEAVING_EARLY_AND_COMING_HOME_LATE, true);
			} else {
				personAttributes.putAttribute(person.getId().toString(), DISLIKES_LEAVING_EARLY_AND_COMING_HOME_LATE, false);
			}
		}

		Controler controler = new Controler(scenario);
		controler.addOverridingModule(new AbstractModule() {
			@Override
			public void install() {
				// We add a class which reacts on people who enter a link and lets it rain on them
				// if we are within a certain time window.
				// The class registers itself as an EventHandler and also produces events by itself.
				bind(RainEngine.class).asEagerSingleton();
			}
		});

		controler.addOverridingModule( new AbstractModule(){
			@Override public void install() {
				this.bindScoringFunctionFactory().toInstance(new RainScoringFunctionFactory(scenario, personAttributes) ) ;
				
			}

		});
		controler.run();
	}

	static class RainEngine implements PersonEntersVehicleEventHandler, LinkEnterEventHandler {

		private EventsManager eventsManager;

		private Map<Id<Vehicle>, Id<Person>> vehicle2driver = new HashMap<>();

		@Inject
		RainEngine(EventsManager eventsManager) {
			this.eventsManager = eventsManager;
			this.eventsManager.addHandler(this);
		}

		@Override 
		public void reset(int iteration) {}

		@Override
		public void handleEvent(PersonEntersVehicleEvent event) {
			vehicle2driver.put(event.getVehicleId(), event.getPersonId());
		}

		@Override
		public void handleEvent(LinkEnterEvent event) {
			if (rainingAt(event.getTime(), event.getLinkId())) {
				eventsManager.processEvent(new RainOnPersonEvent(event.getTime(), vehicle2driver.get(event.getVehicleId())));
			}
		}

		// It starts raining on link 1 at 7:30.
		private boolean rainingAt(double time, Id<Link> linkId) {
			if (time > (7.5 * 60.0 * 60.0) && linkId.toString().equals("1")) {
				return true;
			} else {
				return false;
			}
		}

	}

}
