/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2017 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package org.matsim.codeexamples.network;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.network.io.NetworkWriter;
import org.matsim.utils.objectattributes.attributable.Attributes;

/**
 * @author  jbischoff
 * This provides an example script how to read a MATSim network and modify some values for each link.
 * In this case, we are reducing the capacity of each link by 50%.
 */


/*
 * args[0] path to original network
 * args[1] path to modified network
 */
public class RunModifyNetworkExample {

	public static void main(String[] args) {
		
		// read in the network
		Network network = NetworkUtils.createNetwork();
		//new MatsimNetworkReader(network).readFile("path-to-network.xml");
		new MatsimNetworkReader(network).readFile(args[0]);
		
		// iterate through all links
		for (Link l : network.getLinks().values()){
			//get current capacity
			double oldCapacity = l.getCapacity();
			double oldFreespeed = l.getFreespeed();
			double newCapacity = 0;
			double newFreespeed = 0;
			Set<String> linkType = l.getAllowedModes();
			Set<String> tempt = new HashSet<String>();
			Id<Link> linkid = l.getId();
			Attributes temp = l.getAttributes();
			if(linkType.contains("car")){
				if(oldFreespeed>33){
				//newCapacity = oldCapacity * 0.45  ;
				newFreespeed = oldFreespeed * 0.45  ;
				String s = StringUtils.strip(linkType.toString(),"[]");
				tempt.add(s);
				tempt.add("exp");
				System.out.println(tempt);
			}else{
				//newCapacity = oldCapacity * 0.40  ;
				newFreespeed = oldFreespeed * 0.40  ;
				String s = StringUtils.strip(linkType.toString(),"[]");
				tempt.add(s);
				tempt.add("art");
				System.out.println(tempt);
				
				//linkType.add(s);
			}
			
			//set new capacity
			//l.setCapacity(newCapacity);
			l.setFreespeed(newFreespeed);
			l.setAllowedModes(tempt);
			}
			
		}
		//new NetworkWriter(network).write("path-to-modified-network.xml");
		new NetworkWriter(network).write(args[1]);
	}
}
