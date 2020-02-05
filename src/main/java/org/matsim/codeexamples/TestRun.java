package org.matsim.codeexamples;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.codeexamples.calibration.SPSA;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.network.NetworkChangeEvent;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.NetworkChangeEvent.ChangeType;
import org.matsim.core.network.NetworkChangeEvent.ChangeValue;
import org.matsim.core.network.algorithms.MultimodalNetworkCleaner;
import org.matsim.core.network.algorithms.NetworkCleaner;
import org.matsim.core.network.algorithms.NetworkSimplifier;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.network.io.NetworkWriter;
import org.matsim.core.scenario.ScenarioUtils;

import ch.sbb.matsim.routing.pt.raptor.SwissRailRaptorModule;

public class TestRun {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		int limit = 2;
//		File outpath = new File("D:\\New_calibration\\auto-run\\input");
//		outpath.mkdirs();
		
			
//		for(int k = 0; k < limit; k++){
//			SPSA s = new SPSA();
//			s.run_SPSA(k);
			
		Network network = NetworkUtils.createNetwork();
		new MatsimNetworkReader(network).readFile("D:\\mixed_traffic\\BQX-separated-network.xml");
		NetworkCleaner clean = new NetworkCleaner();
		clean.run(network);
		
		new NetworkWriter(network).write("D:\\mixed_traffic\\new-BQX-separated-network.xml");
		
//		try {
//			FileInputStream fin = new FileInputStream("D:\\New_calibration\\auto-run\\" + (i + 1) + "\\ITERS\\it.1\\BUILT.1.experienced_plans.xml.gz");
//			GZIPInputStream gz = new GZIPInputStream(fin);
//			FileOutputStream fout = new FileOutputStream(outpath + "\\BUILT." + (i + 1) + ".experienced_plans.xml");
//			int count = 0;
//			byte data[] = new byte[1024];
//			while ((count = gz.read(data)) != -1) {
//                fout.write(data, 0, count);
//            }
//            fout.flush();
//            fin.close();
//            fout.close();
//            gz.close();
//            System.out.println("Finished!");		}catch(Exception e){
//			System.out.println("File not found!");
//		}
		
//		if(i > 0 && i + 1 < limit){
//			config.plans().setInputFile("D:\\New_calibration\\auto-run\\" + (i + 1) + "\\ITERS\\it.1\\BUILT.1.experienced_plans.xml.gz");
//		}
		
		
		
		
		
		

		
//		new NetworkSimplifier().run(network);
//		new NetworkWriter(network).write("D:\\New_calibration\\simple_network.xml");
//		
		/*PlanCalcScoreConfigGroup.ModeParams accessWalk = new PlanCalcScoreConfigGroup.ModeParams("access_walk");
		accessWalk.setMarginalUtilityOfTraveling(0);
		PlanCalcScoreConfigGroup.ModeParams egressWalk = new PlanCalcScoreConfigGroup.ModeParams("egress_walk");
		egressWalk.setMarginalUtilityOfTraveling(0);
		PlanCalcScoreConfigGroup.ModeParams transitWalk = new PlanCalcScoreConfigGroup.ModeParams("transit_walk");
		transitWalk.setMarginalUtilityOfTraveling(0);
		//config.plansCalcRoute().setInsertingAccessEgressWalk(true);
		config.planCalcScore().addModeParams(accessWalk);
		config.planCalcScore().addModeParams(egressWalk);
		config.planCalcScore().addModeParams(transitWalk);
		*/
		
		}

}
//}
