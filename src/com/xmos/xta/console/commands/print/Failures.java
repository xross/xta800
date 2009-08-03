package com.xmos.xta.console.commands.print;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.CommandSource;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.routes.Route;
import com.xmos.xta.engine.analysis.routes.RouteTiming;
import com.xmos.xta.engine.analysis.routes.RouteTiming.Level;

public class Failures extends Command {
	public Failures() {
	}

    public int numArgs() { 
    	return 0; 
    }
    
    public String name()        { return "failures"; }        
    public String commandLine() { return "failures"; }        
    
    public String description(Engine engine, boolean detailed) {
    	return "failures: Print all timing failures";
    }

    public Command clone(String[] args, int index) {
   		return new Failures();
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

		if (!validateRoutes(engine, console))
			return;
		
		for (Route route : engine.getRoutes()) {
		    displayRouteSummary(console, route);
		}		
    }

    private void displayRouteSummary(Console console, Route route) {
		RouteTiming routeTiming = route.getRouteTiming();
		CommandSource src = routeTiming.getCommandSource();

		if (!routeTiming.isTimingSpecified() || !routeTiming.passedTiming()) {
			if (src.m_lineNum > 0) {
				console.display(String.format("%s:%d: ",
						src.m_fileName, src.m_lineNum));
			}
			console.display(String.format("error route(%d) %s",
					route.getRouteId(),
			        routeTiming.getDescription(Level.DETAILED)));
		}
	}
}
