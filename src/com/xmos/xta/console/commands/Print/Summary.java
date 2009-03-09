package com.xmos.xta.console.commands.Print;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Route;
import com.xmos.xta.engine.RouteTiming;

public class Summary extends Command {
	public Summary() {}
	
    public int numArgs()        { return 0; }        
    public String name()        { return "summary"; }        
    public String commandLine() { return "summary"; }        
    
    public String description(Engine engine, boolean detailed) {
    	return "summary: Show routes summary";
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
   		return new Summary();
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

		if (!validateRoutes(engine, console))
			return;		    	

		for (Route route : engine.getRoutes()) {
		    displayRouteSummary(console, route);
		}		
		displayOverallSummary(console, engine);
    }

    private void displayRouteSummary(Console console, Route route) {
    	RouteTiming routeTiming = route.getRouteTiming();
    	    	
    	console.display(String.format("Route(%d) %s\n", 
    			route.getRouteId(), route.getDescription()));
    	
		if (routeTiming.isTimingSpecified()) {
			if (routeTiming.passedTiming()) {
		    	console.display(String.format("    Pass, Slack: %.1fns, ", routeTiming.getSlackTime())); 						
			} else {
		    	console.display(String.format("    Fail, Violation: %.1fns, ", routeTiming.getViolationTime())); 						
			}
	    	console.display(String.format("Required: %.1fns, Worst: %.1fns, Min Frequency: %d Mhz\n", 
	    			routeTiming.getMaxTime(), routeTiming.getWorstTime(), routeTiming.getMinFrequency())); 				
		} else {
			console.display("    No Timing Specified\n");
		}
		
    	console.display(String.format("    Complete paths: %d, Unresolved paths: %d\n\n", 
    			route.getCompletedPaths().size(), route.getUnresolvedPaths().size()));
    }
    
    private void displayOverallSummary(Console console, Engine engine) {
		double minSlackTime = Double.MAX_VALUE;
		double maxViolationTime = 0.0;
		
		int numFails = 0;
		int numUnspecified = 0;
		int total = 0;
		int minFrequency = 0;
		
		for (Route route : engine.getRoutes()) {
			RouteTiming routeTiming = route.getRouteTiming();
				
			if (routeTiming.isTimingSpecified()) {			
				if (routeTiming.passedTiming()) {
					double slackTime = routeTiming.getSlackTime();
					if (slackTime < minSlackTime) {
						minSlackTime = slackTime;
						minFrequency = routeTiming.getMinFrequency();
					}
				} else {
					double violationTime = routeTiming.getViolationTime();				
					if (violationTime > maxViolationTime) {
						maxViolationTime = violationTime;
					}
					++numFails;				
				}
			} else {
				++numUnspecified;
			}
			++total;
		}
		
		if (numUnspecified == 0) {
			if (numFails > 0) {
			    console.display(String.format("Fail, %d/%d, Max Violation: %.1fns\n", 
			    		numFails, total, maxViolationTime));
			} else {
				console.display(String.format("Pass, Min Slack: %.1fns, Min Frequency: %d Mhz\n", 
			    		minSlackTime, minFrequency));			
			}
		} else {
		    console.display("Fail, Timing Unspecified\n");
		}
    }
}
