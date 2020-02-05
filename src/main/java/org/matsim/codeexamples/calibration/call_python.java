package org.matsim.codeexamples.calibration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class call_python {

	public void run(String path, int k, int i){
		Process proc;
		try {
			String input = "python ";
			input = input.concat(path).concat(" ").concat(String.valueOf(k)).concat(" ").concat(String.valueOf(i)) ;
			proc = Runtime.getRuntime().exec(input);// 执行py文件
			System.out.println("Run Python.");
			//用输入输出流来截取结果
			BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				System.out.println(line);
			}
			in.close();
			proc.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		call_python p1 = new call_python();
		for(int i = 0; i < 5; i++){
			for(int j = 0; j < 2; j++){
				p1.run("D:\\yh1995\\auto-run\\calibration_analysis.py",i,j);
			}
		}
		
		//p1.run("D:\\yh1995\\auto-run\\calibration_analysis.py",4,0);
		
	}

}
