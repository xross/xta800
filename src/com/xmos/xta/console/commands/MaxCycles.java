package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Block;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Route;

public class MaxCycles extends Command  {
	private int m_maxCycles;

	MaxCycles() {
		m_maxCycles = 0;
	}
	
	MaxCycles(int maxCycles) {
		m_maxCycles = maxCycles;
	}
	
    public int numArgs() {
    	return 1;
    }        

    public String name() {
    	return "maxcycles";
    }        

    public String commandLine() {
    	return "maxcycles " + m_maxCycles;
    }
    
    public String description(Engine engine, boolean detailed) {
        return "maxcycles <num cycles> : Analysis fails at the given cycle limit";      	
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {    	    	
		if ((index + numArgs()) >= args.length) 
			return null;
		
    	int cycles;
    	try { cycles = Integer.parseInt(args[index + 1]); }
	    catch (NumberFormatException e) {
	        return null;
	    }		
		return new MaxCycles(cycles);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;		
		
		Route route = engine.getRoute();
		if(route == null || route.completePaths().size() == 0){
			console.display("FAIL\n");
			console.display("no paths found\n");
    		return;
		}
		double worstPathCycles = 0;
        for(Block path : route.completePaths()){
        	double cycles = path.getNumCycles(true);
        	worstPathCycles = Math.max(worstPathCycles, cycles);
        }
        if(worstPathCycles > m_maxCycles){
        	console.display("FAIL\n");
        	console.display("" + (worstPathCycles - m_maxCycles) + " cycles violation\n");
        } else {
        	console.display("PASS\n");
        	console.display("" + (m_maxCycles - worstPathCycles) + " cycles slack\n");
        }
   }    
}
