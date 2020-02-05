package org.matsim.codeexamples.calibration;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.matsim.codeexamples.Run;



public class SPSA {
	
	public static double loss(int k, int col) throws IOException{
		double  y = 0;
		
		String line;
		ArrayList<Double> sim_vol = new ArrayList<>();
		ArrayList<Double> real_vol = new ArrayList<>();
		ArrayList<Double> sim_spd = new ArrayList<>();
		ArrayList<Double> real_spd = new ArrayList<>();
		
		
		// unzip link stats
		FileInputStream fin = new FileInputStream("D:\\New_calibration\\auto-run\\" + k + "\\" + col + "\\ITERS\\it.2\\BUILT.2.linkstats.txt.gz");
		GZIPInputStream gz = new GZIPInputStream(fin);
		FileOutputStream fout = new FileOutputStream("D:\\New_calibration\\auto-run\\" + k + "\\" + col + "\\ITERS\\it.2\\BUILT.2.linkstats.txt");
		int count = 0;
		byte data[] = new byte[1024];
		while ((count = gz.read(data)) != -1) {
            fout.write(data, 0, count);
        }
        fout.flush();
        fin.close();
        fout.close();
        gz.close();
        System.out.println("Unzip finished");
        
        call_python p1 = new call_python();
		p1.run("D:\\calibration_analysis.py",k,col);

		
		File f1 = new File("D:\\New_calibration\\auto-run\\" + k + "\\" + col + "\\link_volume.csv");
		File f2 = new File("D:\\New_calibration\\auto-run\\" + k + "\\" + col + "\\link_speed.csv");
		
		BufferedReader in = new BufferedReader(new FileReader(f1));
		BufferedReader in1 = new BufferedReader(new FileReader(f2));
		in.readLine();
		in1.readLine();
		
		while((line = in.readLine()) != null){
			String [] item = line.split(",");
			sim_vol.add(Double.parseDouble(item[3]));
			real_vol.add(Double.parseDouble(item[4]));
			
		}
//		double[] sim_vol1 = new double[sim_vol.size()];
//		double[] real_vol1 = new double[real_vol.size()];
//		sim_vol.toArray(sim_vol1);
//		real_vol.toArray(real_vol1);
//		
		while((line = in1.readLine()) != null){
			String [] item = line.split(",");
			sim_spd.add(Double.parseDouble(item[2]));
			real_spd.add(Double.parseDouble(item[3]));
		}
//		String[] sim_spd1 = new String[sim_spd.size()];
//		String[] real_spd1 = new String[real_spd.size()];
//		sim_vol.toArray(sim_spd1);
//		real_vol.toArray(real_spd1);

		//System.out.println(real_vol + "," + real_vol.size());
		
		in.close();
		in1.close();
		for(int i = 0;i<sim_vol.size();i++){
			y += Math.abs(sim_vol.get(i) - real_vol.get(i))/real_vol.get(i);
		}
		
		for(int i = 0; i < sim_spd.size(); i++){
			y += Math.abs(sim_spd.get(i) - real_spd.get(i))/real_spd.get(i);
		}
		
		
		return y;
	}

	public void run_SPSA(int k) throws IOException{
		int limit = 1;
		double alpha = 0.602;
		double gamma = 0.101;
		int p = 24;
		double a = 0.16;
		double A = 100;
		double c = 0.1;
		double [] theta = new double [24];
		double [] delta = new double [24];
		double ak = a/Math.pow((k+A+1), alpha);
		double ck = c/Math.pow(k+1, gamma);
		double [][] theta_temp = new double [24][2];
		double [] ghat = new double[24];
		
		
		String line;
		String path;
		
		
		
		if(k == 0)
			path = "D:\\New_calibration\\auto-run\\initial_theta.csv";
		else
			path = "D:\\New_calibration\\auto-run\\" + (k - 1) + "\\theta_final" + (k-1) + ".csv";
		
		File file = new File("D:\\New_calibration\\auto-run\\" + k);
		if(!file.exists())
			file.mkdir();
		File f0 = new File(path);
		File f1 = new File("D:\\New_calibration\\auto-run\\" + k + "\\theta_up_down" + k + ".csv");
		File f2 = new File("D:\\New_calibration\\auto-run\\" + k + "\\theta_final" + k + ".csv");
		
		BufferedReader in0 = new BufferedReader(new FileReader(f0));
		BufferedWriter out1 = new BufferedWriter(new FileWriter(f1));
		BufferedWriter out2 = new BufferedWriter(new FileWriter(f2));
		
		int count = 0;
		while((line = in0.readLine()) != null){
			String [] item = line.split(",");
			theta[count] = Double.parseDouble(item[0]);
			//System.out.print(theta[count] + ",");
			count++;
		}
		

		//System.out.println(real_vol + "," + real_vol.size());
		
		
		in0.close();
		
		
		
		
		for(int i = 0; i < limit; i++){
			ak = a/Math.pow((i+1+A),alpha);
			ck = c/Math.pow((i+1), gamma);
			for(int j = 0;j < 24;j++){
				delta[j] = 2*Math.round(Math.random()) - 1;
				//System.out.println(delta[j]);
			}
			for(int j = 0;j < 24;j++){
				theta_temp[j][0] = theta[j] + ck * delta[j] > 1 ? 1 : theta[j] + ck * delta[j] ;
				theta_temp[j][1] = theta[j] - ck * delta[j] < 0.1 ? 0.1 : theta[j] - ck * delta[j] ;
				
				out1.write(theta_temp[j][0]+","+theta_temp[j][1]+"\n");
				System.out.println(theta_temp[j][0]+","+theta_temp[j][1]);
			}
			out1.close();
			
			System.out.println("Run MATSim");
			Run r = new Run();
			r.run_MATSim(k, "D:\\New_calibration\\config-test.xml");
			
			
			double y_plus = loss(k,0);
			double y_minus = loss(k,1);
	        for(int j = 0; j < 24 ; j++){
	        	ghat[j] = (y_plus - y_minus) / (2 * ck * delta[j]);
	        	theta[j] = (theta[j] - ak * ghat[j]) < 0.1 ? 0.1 : ((theta[j] - ak * ghat[j]) > 1 ? 1 : (theta[j] - ak * ghat[j]));
	        	//theta[j] = (theta[j] - ak * ghat[j]);
	        	System.out.println(theta[j]);
	        	//System.out.println(ck + "," + ak + "," + ghat[j]);
	        	out2.write(theta[j] + "\n");
	        }
			
	        out2.close();
			
				
		}
		
		
		System.out.println("Generated new theta!");
		
		
//		call_python p1 = new call_python();
//		p1.run("D:\\calibration_analysis.py");
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		int k = 0;
		SPSA s = new SPSA();
		s.run_SPSA(k);
		
	}

}
