package org.matsim.codeexamples;

import java.io.IOException;

import org.matsim.codeexamples.calibration.SPSA;

public class TestRun {
	
	public static void main(String args[]) throws IOException{
		int limit = 6;
		SPSA s = new SPSA();
		for(int k = 4; k < limit ; k++){
			s.run_SPSA(k);
		}
	}

}
