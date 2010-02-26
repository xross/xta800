package com.xmos.xta.core.consoletest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Random;

import com.xmos.xta.core.consoletest.commands.Add;
import com.xmos.xta.core.consoletest.commands.Analyse;
import com.xmos.xta.core.consoletest.commands.Clear;
import com.xmos.xta.core.consoletest.commands.Command;
import com.xmos.xta.core.consoletest.commands.Config;
import com.xmos.xta.core.consoletest.commands.Echo;
import com.xmos.xta.core.consoletest.commands.Exit;
import com.xmos.xta.core.consoletest.commands.Help;
import com.xmos.xta.core.consoletest.commands.History;
import com.xmos.xta.core.consoletest.commands.List;
import com.xmos.xta.core.consoletest.commands.Load;
import com.xmos.xta.core.consoletest.commands.Mess;
import com.xmos.xta.core.consoletest.commands.Pling;
import com.xmos.xta.core.consoletest.commands.Print;
import com.xmos.xta.core.consoletest.commands.Pwd;
import com.xmos.xta.core.consoletest.commands.Remove;
import com.xmos.xta.core.consoletest.commands.Scripter;
import com.xmos.xta.core.consoletest.commands.Set;
import com.xmos.xta.core.consoletest.commands.Source;
import com.xmos.xta.core.consoletest.commands.Status;
import com.xmos.xta.core.consoletest.commands.Version;

public class ConsoleTest {

	private static Command[] realCommands = { new Add(), new Analyse(),
			new Config(), new Clear(), new Echo(), new Help(), new History(),
			new List(), 
			//new Load("test.xe"), 
			new Mess(), new Print(),
			new Pwd(), new Remove(), new Scripter(), new Set(), new Source(),
			new Status(), new Version() , new Pling()};			

	private HashSet<String> m_errorToFile;
	
	
	public static void main(String[] args) {
		ConsoleTest ct = new ConsoleTest();
		ct.test();
	}

	public void test(){
		m_errorToFile = new HashSet<String>();
		Random r = new Random();
		StringBuffer sb = new StringBuffer();
		
		int iteration = 0;
		while(true){
			
			
			makeCmdList(r, sb);
			
	        writeOutCmdFile(sb); 
	        
	        run();
	        
	        detectErrors(iteration);
	        iteration++;
	        sb = new StringBuffer();
		}
	}

	private void detectErrors(int i) {
		String error = "";
        try {
	        FileReader fr = new FileReader("output.txt");
	        BufferedReader br = new BufferedReader(fr);
	        String s;
	        
	        while((s = br.readLine()) != null) {
	        	
	        	if(s.contains("com.xmos.xta") || s.contains("java.")){
	        		error += s +"\n";
	        	}
	        }
	        fr.close(); 
        } catch (IOException e) {
			e.printStackTrace();
		}
        if(!error.equals("") && !m_errorToFile.contains(error)){
        	m_errorToFile.add(error);
        	System.out.println(error);
        	try {
    			Process p = Runtime.getRuntime().exec(
    					"cp cmds.txt cmds" + i +".txt");
    			p.waitFor();
    		} catch (Exception err) {
    			err.printStackTrace();
    		}
        }
	}

	private void writeOutCmdFile(StringBuffer sb) {
		FileWriter outputStream = null;
        PrintWriter printer = null;
        try {
            outputStream = new FileWriter("cmds.txt");
            printer = new PrintWriter(outputStream);
            printer.println(sb.toString());

            if (outputStream != null) {
                outputStream.close();
            }
        } catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void makeCmdList(Random r, StringBuffer sb) {
		
		Command load = new Load("test.xe");
		for(int i=0;i<10;i++){
			sb.append(load.getCommand());
		}
		
		for(int i=0;i<100;i++){
			if(r.nextBoolean()){
				sb.append("-");
			}
			int j= r.nextInt(realCommands.length);
			sb.append(realCommands[j].getCommand());
			sb.append("\n");
		}
		
		Exit exit = new Exit();
		for(int i=0;i<10;i++){
			sb.append(exit.getCommand());
			sb.append("\n");
		}

	}

	private boolean run() {
		try {
			Process p = Runtime.getRuntime().exec(
					"xta -source cmds.txt > output.txt 2>&1");
			p.waitFor();
		} catch (Exception err) {
			err.printStackTrace();
			return false;
		}
		return true;
	}
}
