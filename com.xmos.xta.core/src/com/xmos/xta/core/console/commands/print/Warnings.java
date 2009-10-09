package com.xmos.xta.core.console.commands.print;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.routes.RouteTiming;
import com.xmos.xta.core.engine.analysis.routes.RouteTiming.Level;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.Unknown.State;

public class Warnings extends Command {
	public Warnings() {
	}

    public int numArgs() { 
    	return 0; 
    }
    
    public String name()        { return "warnings"; }        
    public String commandLine() { return "warnings"; }        
    
    public String description(Engine engine, boolean detailed) {
    	return "warnings: Print all timing warnings";
    }

    public Command clone(String[] args, int index) {
   		return new Warnings();
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		for (Route route : engine.getRoutes()) {
		    displayRouteSummary(engine, console, route);
		}		
    }

    private void displayRouteSummary(Engine engine, Console console, Route route) {
		RouteTiming routeTiming = route.getRouteTiming();
		CommandSource src = routeTiming.getCommandSource();

		if (!routeTiming.isTimingSpecified() || !routeTiming.passedTiming() ||
			(route.getRouteUnknowns().getUnknowns(State.UNKNOWN).size() > 0)) {
			
			// Need to take the '\n' off the end of the string
			String description = routeTiming.getDescription(Level.DETAILED);
			description = description.substring(0, description.length() - 1);
			
			engine.notify(src, Notifier.NOTIFY_WARNING, 
					String.format("route(%d) %s", route.getRouteId(), description));
		}
	}
}
