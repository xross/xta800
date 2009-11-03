package com.xmos.xta.core.console.commands.exit;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.routes.RouteTiming;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown.State;

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
		boolean warnAsErrors = engine.getConfig().m_warnAsErrors;
		boolean timingErrors = engine.getConfig().m_timingFailuresAsErrors;
		int errors = 0;

		if (engine.getNumErrors() > 0) {
			errors = 1;
			
    	} else if (timingErrors) {
			for (Route route : engine.getRoutes()) {
				RouteTiming routeTiming = route.getRouteTiming();
					
				if (routeTiming.isTimingSpecified()) {			
					if (!routeTiming.passedTiming()) {
						errors = 1;
				    	break;
					}
				} else {
			    	errors = 1;
			    	break;
				}
				
				if (warnAsErrors) {
					if (route.getRouteUnknowns().getUnknowns(State.UNKNOWN).size() > 0) {
						errors = 1;
						break;
					}
				}
			}
    	}
    	shell.exit(errors);
    }    
}
