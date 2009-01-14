package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Block;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Route;

public class MaxTime extends Command  {
	private int m_maxTime;

	MaxTime() {
		m_maxTime = 0;
	}
	
	MaxTime(int maxTime) {
		m_maxTime = maxTime;
	}
	
    public int numArgs() {
    	return 1;
    }        

    public String name() {
    	return "maxtime";
    }        

    public String commandLine() {
    	return "maxtime " + m_maxTime;
    }
    
    public String description(Engine engine, boolean detailed) {
        return "maxtime <ns> : Analysis fails at the given time limit";      	
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {    	    	
		if ((index + numArgs()) >= args.length) 
			return null;

    	int time;
    	try { time = Integer.parseInt(args[index + 1]); }
	    catch (NumberFormatException e) {
	        return null;
	    }		
		return new MaxTime(time);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;		
		
		Route route = engine.getRoute();
        for(Block path : route.completePaths()){
        	double time = path.getTimeTaken(true);
        	if (time > m_maxTime){
        		console.display("FAIL\n");
        		return;
        	}
        }
        console.display("PASS\n");
        return;
   }    
}
