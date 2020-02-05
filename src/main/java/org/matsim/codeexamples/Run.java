package org.matsim.codeexamples;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Set;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.network.NetworkChangeEvent;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.NetworkChangeEvent.ChangeType;
import org.matsim.core.network.NetworkChangeEvent.ChangeValue;
import org.matsim.core.scenario.ScenarioUtils;

import ch.sbb.matsim.routing.pt.raptor.SwissRailRaptorModule;

public class Run {

	public void run_MATSim(int k, String path){
		
		double ExpressFactor []= new double [6];
		double ArterialFactor []= new double [6];
		double ExpressFactor1 []= new double [6];
		double ArterialFactor1 []= new double [6];  
		
		try{
			BufferedReader reader = new BufferedReader(new FileReader("D:\\New_calibration\\auto-run\\" + k
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
					ExpressFactor1[j]=param[j+12][col];
					ArterialFactor1[j]=param[j+18][col];
					System.out.println(ExpressFactor[j]+","+ArterialFactor[j]+","+ExpressFactor1[j]+","+ArterialFactor1[j]);
					
				}
				Config config = ConfigUtils.loadConfig(path);
				config.network().setTimeVariantNetwork(true);
				config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);
				config.controler().setOutputDirectory("D:\\New_calibration\\auto-run\\" + k + "\\" + col);
				
				Scenario scenario = ScenarioUtils.loadScenario(config) ;
				Network network = scenario.getNetwork();
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
								event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, speed*ArterialFactor1[5] ));
								event.addLink(link);
								NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(),event);
							}
							{
								NetworkChangeEvent event = new NetworkChangeEvent(7.*3600.) ;
								//event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS,  threshold/10 ));
								event.setFlowCapacityChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, capacity/3600*ArterialFactor[0] ));
								event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, speed*ArterialFactor1[0] ));
								event.addLink(link);
								NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(),event);
							}
							{
								NetworkChangeEvent event = new NetworkChangeEvent(10.*3600.) ;
								//event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS,  threshold/10 ));
								event.setFlowCapacityChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, capacity/3600*ArterialFactor[1] ));
								event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, speed*ArterialFactor1[1] ));
								event.addLink(link);
								NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(),event);
							}
							{
								NetworkChangeEvent event = new NetworkChangeEvent(13.*3600.) ;
								//event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS,  threshold/10 ));
								event.setFlowCapacityChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, capacity/3600*ArterialFactor[2] ));
								event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, speed*ArterialFactor1[2] ));
								event.addLink(link);
								NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(),event);
							}
							{
								NetworkChangeEvent event = new NetworkChangeEvent(16.*3600.) ;
								//event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS,  threshold/10 ));
								event.setFlowCapacityChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, capacity/3600*ArterialFactor[3] ));
								event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, speed*ArterialFactor1[3] ));
								event.addLink(link);
								NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(),event);
							}
							{
								NetworkChangeEvent event = new NetworkChangeEvent(19.*3600.) ;
								//event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS,  threshold/10 ));
								event.setFlowCapacityChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, capacity/3600*ArterialFactor[4] ));
								event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, speed*ArterialFactor1[4] ));
								event.addLink(link);
								NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(),event);
							}
							{
								NetworkChangeEvent event = new NetworkChangeEvent(22.*3600.) ;
								//event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS,  threshold/10 ));
								event.setFlowCapacityChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, capacity/3600*ArterialFactor[5] ));
								event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, speed*ArterialFactor1[5] ));
								event.addLink(link);
								NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(),event);
							}
						}
					}
					}
					
				
				// ---
				
				controler.run();
			}
			
			
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
