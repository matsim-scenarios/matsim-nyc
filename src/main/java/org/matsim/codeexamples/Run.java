package org.matsim.codeexamples;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.events.PersonLeavesVehicleEvent;
import org.matsim.api.core.v01.events.handler.LinkLeaveEventHandler;
import org.matsim.api.core.v01.events.handler.PersonLeavesVehicleEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Person;
import org.matsim.codeexamples.scoring.example16customscoring.PersonLeavesFHVEvent;
import org.matsim.codeexamples.scoring.example16customscoring.PersonLeavesTaxiEvent;
import org.matsim.codeexamples.scoring.example16customscoring.PersonParkingEvent;
import org.matsim.codeexamples.scoring.example16customscoring.TollPersonEvent1;
import org.matsim.codeexamples.scoring.example16customscoring.TollPersonEvent2;
import org.matsim.codeexamples.scoring.example16customscoring.RunCustomScoringExampleTaxi.TaxiScoringFunctionFactory;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.network.NetworkChangeEvent;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.NetworkChangeEvent.ChangeType;
import org.matsim.core.network.NetworkChangeEvent.ChangeValue;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.vehicles.Vehicle;

import ch.sbb.matsim.routing.pt.raptor.SwissRailRaptorModule;

public class Run {
	
	public static String [] toll1 = {"320888283_0","60325668_0","415882710_0","232473896_0","49032026_0","644974431_0","360639917_1"};
	public static String [] toll2 = {"40596823_0","25154607_0","40595084_0","375623292_0","25464382_0","5669174_0","90086370_0","5699313_0","413749473_0","5681925_0","11878036_0","46469739_0"};
	
	static class ScoreEngine implements PersonLeavesVehicleEventHandler, LinkLeaveEventHandler {

		private EventsManager eventsManager;

		private Map<Id<Vehicle>, Id<Person>> vehicle2driver = new HashMap<>();

		@Inject
		ScoreEngine(EventsManager eventsManager) {
			this.eventsManager = eventsManager;
			this.eventsManager.addHandler(this);
		}

		@Override 
		public void reset(int iteration) {}

		@Override
		public void handleEvent(PersonLeavesVehicleEvent event) {
			vehicle2driver.put(event.getVehicleId(), event.getPersonId());
			if (parkingAt(event.getTime(), event.getPersonId(),event.getVehicleId())) {
				eventsManager.processEvent(new PersonParkingEvent(event.getTime(), vehicle2driver.get(event.getVehicleId())));
				//System.out.println("park");
			}else if (TaxiAt(event.getTime(),event.getVehicleId())) {
				eventsManager.processEvent(new PersonLeavesTaxiEvent(event.getTime(), vehicle2driver.get(event.getVehicleId())));
				//System.out.println("taxi");
			}else if (FHVAt(event.getTime(),event.getVehicleId())) {
				eventsManager.processEvent(new PersonLeavesFHVEvent(event.getTime(), vehicle2driver.get(event.getVehicleId())));
				//System.out.println("FHV");
			}
		}

		@Override
		public void handleEvent(LinkLeaveEvent event) {
			if (TollAt(event.getTime(), event.getLinkId())==1) {
				eventsManager.processEvent(new TollPersonEvent1(event.getTime(), vehicle2driver.get(event.getVehicleId())));
				
				//System.out.println("vehicleID:"+vehicle2driver.get(event.getVehicleId()));
			}else if(TollAt(event.getTime(), event.getLinkId())==2){
				eventsManager.processEvent(new TollPersonEvent2(event.getTime(), vehicle2driver.get(event.getVehicleId())));
			}
		}
		
		//private boolean parkingAt_1(double time, Id<Link> linkId){
			
		//}
		// It starts raining on link 1 at 7:30.
		private boolean parkingAt(double time, Id<Person> personId, Id<Vehicle> vehicleId) {
			
			if (time > (0.0 * 60.0 * 60.0) && personId.equals(vehicleId)) {
				return true;
			} else {
				return false;
			}
			
		}
			
		private boolean TaxiAt(double time, Id<Vehicle> vehicleId) {
			
			if (time > (0.0 * 60.0 * 60.0) && vehicleId.toString().contains("taxi")) {
				return true;
			} else {
				return false;
			}
			
		}

		private boolean FHVAt(double time, Id<Vehicle> vehicleId) {
	
			if (time > (0.0 * 60.0 * 60.0) && vehicleId.toString().contains("FHV")) {
				return true;
			} else {
				return false;
			}
	
		}	
		
		private int TollAt(double time, Id<Link> LinkId) {
			List<String> list1=Arrays.asList(toll1);
			List<String> list2=Arrays.asList(toll2);
			int i = 0;
			if (list1.contains(LinkId.toString())) {
				//System.out.println(LinkId.toString());
				i = 1;
			} else if(list2.contains(LinkId.toString())){
				i = 2;
			}
			return i;
		}


	}
	

	public void run_MATSim(int k, String path){
		
		double ExpressFactor []= new double [6];
		double ArterialFactor []= new double [6];
		double ExpressFactor1 []= {0.472941,0.497564,0.502572, 0.435369, 0.484185, 0.568571}; // 33.33m/s 
		double ArterialFactor1 []= {0.276634, 0.265192,0.261024, 0.254357,0.279059,0.308127}; //22.22 m/s
		double ArterialFactor2 []= {0.409828,0.392877,0.386702,0.376825,0.413420,0.456485};// 15 m/s
		double ArterialFactor3 []= {0.737691,0.707178,0.696064, 0.678285,0.744156, 0.821673}; //8.333 m/s
		
		try{
			BufferedReader reader = new BufferedReader(new FileReader("D:\\yh1995\\auto-run\\" + k
					+ "\\theta_up_down" + k + ".csv"));
			String line=null;
			int i = 0;
			double [][] param = new double [24][2];
			
			while((line=reader.readLine())!=null){
				String temp [] = line.split(",");
				param[i][0]=Double.parseDouble(temp[0]);
				param[i][1]=Double.parseDouble(temp[1]);
				i+=1;
			}
			System.out.println("Theta loaded");
			reader.close();
			
			for(int col = 0; col < 2; col++){
				for(int j=0;j<6;j++ ){
					ExpressFactor[j]=param[j][col];
					ArterialFactor[j]=param[j+6][col];
//					ExpressFactor1[j]=param[j+12][col];
//					ArterialFactor1[j]=param[j+18][col];
					//System.out.println(ExpressFactor[j]+","+ArterialFactor[j]+","+ExpressFactor1[j]+","+ArterialFactor1[j]);
					System.out.println(ExpressFactor[j]+","+ArterialFactor[j]);
					
				}
				Config config = ConfigUtils.loadConfig(path);
				config.network().setTimeVariantNetwork(true);
				config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);
				config.controler().setOutputDirectory("D:\\yh1995\\auto-run\\" + k + "\\" + col);
				
				Scenario scenario = ScenarioUtils.loadScenario(config) ;
				//Network network = scenario.getNetwork();
				Controler controler = new Controler( scenario ) ;
				controler.addOverridingModule(new SwissRailRaptorModule());
				
				for ( Link link : scenario.getNetwork().getLinks().values() ) {
					double speed = link.getFreespeed() ;
					//final double threshold = 5./3.6;
					double capacity = link.getCapacity() ;
					Set<String> linkType = link.getAllowedModes();
	
					if(linkType.contains("car")){
						if ( speed > 33 ) {
							{
								NetworkChangeEvent event = new NetworkChangeEvent(0.*3600.) ;
								//event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS,  threshold/10 ));
								event.setFlowCapacityChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, capacity/3600*ExpressFactor[5] ));
								event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, speed*ExpressFactor1[5] ));
								event.addLink(link);
								NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(),event);
							}
							{
								NetworkChangeEvent event = new NetworkChangeEvent(7.*3600.) ;
								//event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS,  threshold/10 ));
								event.setFlowCapacityChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, capacity/3600*ExpressFactor[0] ));
								event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, speed*ExpressFactor1[0] ));
								event.addLink(link);
								NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(),event);
							}
							{
								NetworkChangeEvent event = new NetworkChangeEvent(10.*3600.) ;
								//event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS,  threshold/10 ));
								event.setFlowCapacityChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, capacity/3600*ExpressFactor[1] ));
								event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, speed*ExpressFactor1[1] ));
								event.addLink(link);
								NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(),event);
							}
							{
								NetworkChangeEvent event = new NetworkChangeEvent(13.*3600.) ;
								//event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS,  threshold/10 ));
								event.setFlowCapacityChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, capacity/3600*ExpressFactor[2] ));
								event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, speed*ExpressFactor1[2] ));
								event.addLink(link);
								NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(),event);
							}
							{
								NetworkChangeEvent event = new NetworkChangeEvent(16.*3600.) ;
								//event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS,  threshold/10 ));
								event.setFlowCapacityChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, capacity/3600*ExpressFactor[3] ));
								event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, speed*ExpressFactor1[3] ));
								event.addLink(link);
								NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(),event);
							}
							{
								NetworkChangeEvent event = new NetworkChangeEvent(19.*3600.) ;
								//event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS,  threshold/10 ));
								event.setFlowCapacityChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, capacity/3600*ExpressFactor[4] ));
								event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, speed*ExpressFactor1[4] ));
								event.addLink(link);
								NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(),event);
							}
							{
								NetworkChangeEvent event = new NetworkChangeEvent(22.*3600.) ;
								//event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS,  threshold/10 ));
								event.setFlowCapacityChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, capacity/3600*ExpressFactor[5] ));
								event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, speed*ExpressFactor1[5] ));
								event.addLink(link);
								NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(),event);
							}
						}else{
							{
								NetworkChangeEvent event = new NetworkChangeEvent(0.*3600.) ;
								//event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS,  threshold/10 ));
								event.setFlowCapacityChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, capacity/3600*ArterialFactor[5] ));
								if(speed <= 33 && speed >22){
									event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, speed*ArterialFactor1[5] ));
								}else if(speed <= 22 && speed >10){
									event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, speed*ArterialFactor2[5] ));
								}else if(speed < 10){
									event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, speed*ArterialFactor3[5] ));
								}
								event.addLink(link);
								NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(),event);
							}
							{
								NetworkChangeEvent event = new NetworkChangeEvent(7.*3600.) ;
								//event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS,  threshold/10 ));
								event.setFlowCapacityChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, capacity/3600*ArterialFactor[0] ));
								if(speed <= 33 && speed >22){
									event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, speed*ArterialFactor1[0] ));
								}else if(speed <= 22 && speed >10){
									event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, speed*ArterialFactor2[0] ));
								}else if(speed < 10){
									event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, speed*ArterialFactor3[0] ));
								}event.addLink(link);
								NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(),event);
							}
							{
								NetworkChangeEvent event = new NetworkChangeEvent(10.*3600.) ;
								//event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS,  threshold/10 ));
								event.setFlowCapacityChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, capacity/3600*ArterialFactor[1] ));
								if(speed <= 33 && speed >22){
									event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, speed*ArterialFactor1[1] ));
								}else if(speed <= 22 && speed >10){
									event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, speed*ArterialFactor2[1] ));
								}else if(speed < 10){
									event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, speed*ArterialFactor3[1] ));
								}event.addLink(link);
								NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(),event);
							}
							{
								NetworkChangeEvent event = new NetworkChangeEvent(13.*3600.) ;
								//event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS,  threshold/10 ));
								event.setFlowCapacityChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, capacity/3600*ArterialFactor[2] ));
								if(speed <= 33 && speed >22){
									event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, speed*ArterialFactor1[2] ));
								}else if(speed <= 22 && speed >10){
									event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, speed*ArterialFactor2[2] ));
								}else if(speed < 10){
									event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, speed*ArterialFactor3[2] ));
								}event.addLink(link);
								NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(),event);
							}
							{
								NetworkChangeEvent event = new NetworkChangeEvent(16.*3600.) ;
								//event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS,  threshold/10 ));
								event.setFlowCapacityChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, capacity/3600*ArterialFactor[3] ));
								if(speed <= 33 && speed >22){
									event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, speed*ArterialFactor1[3] ));
								}else if(speed <= 22 && speed >10){
									event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, speed*ArterialFactor2[3] ));
								}else if(speed < 10){
									event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, speed*ArterialFactor3[3] ));
								}event.addLink(link);
								NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(),event);
							}
							{
								NetworkChangeEvent event = new NetworkChangeEvent(19.*3600.) ;
								//event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS,  threshold/10 ));
								event.setFlowCapacityChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, capacity/3600*ArterialFactor[4] ));
								if(speed <= 33 && speed >22){
									event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, speed*ArterialFactor1[4] ));
								}else if(speed <= 22 && speed >10){
									event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, speed*ArterialFactor2[4] ));
								}else if(speed < 10){
									event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, speed*ArterialFactor3[4] ));
								}event.addLink(link);
								NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(),event);
							}
							{
								NetworkChangeEvent event = new NetworkChangeEvent(22.*3600.) ;
								//event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS,  threshold/10 ));
								event.setFlowCapacityChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, capacity/3600*ArterialFactor[5] ));
								if(speed <= 33 && speed >22){
									event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, speed*ArterialFactor1[5] ));
								}else if(speed <= 22 && speed >10){
									event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, speed*ArterialFactor2[5] ));
								}else if(speed < 10){
									event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, speed*ArterialFactor3[5] ));
								}event.addLink(link);
								NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(),event);
							}
						}
					}
					}
					
				
				// ---
				controler.addOverridingModule(new AbstractModule() {
					@Override
					public void install() {
						// We add a class which reacts on people who enter a link and lets it rain on them
						// if we are within a certain time window.
						// The class registers itself as an EventHandler and also produces events by itself.
						
						bind(ScoreEngine.class).asEagerSingleton();
						//bind(TaxiEngine.class).asEagerSingleton();
					}
				});

				controler.addOverridingModule( new AbstractModule(){
					@Override public void install() {
						
						this.bindScoringFunctionFactory().toInstance(new TaxiScoringFunctionFactory(scenario) ) ;
						//this.bindScoringFunctionFactory().toInstance(new TaxiScoringFunctionFactory(scenario) ) ;
					}

				});
				controler.run();
			}
			
			
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
