package com.xmos.xta.console.commands.exit;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.routes.Route;
import com.xmos.xta.engine.analysis.routes.RouteTiming;

public class Exit extends Command {
	public Exit() {		
	}
	
    public int numArgs() {
    	return 0;
    }

    public String name() {
    	return "exit";
    }        

    public String commandLine() {
    	return "exit";
    }
    
    public String description(Engine engine, boolean detailed) {
        return "exit: Quits the shell (Standalone mode only)";      	
    }

    public Command clone(String[] args, int index) {
    	return new Exit();
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		int numFailures = 0;
		for (Route route : engine.getRoutes()) {
			RouteTiming routeTiming = route.getRouteTiming();
				
			if (routeTiming.isTimingSpecified()) {			
				if (!routeTiming.passedTiming()) {
					++numFailures;				
				}
			} else {
				++numFailures;
			}
		}    	    		
    	shell.exit(numFailures);
    }    
}
