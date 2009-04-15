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

    public Command clone(String[] args, int index) {
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
    	console.display(route.getFullDescription());
    }
    
    private void displayOverallSummary(Console console, Engine engine) {
		double maxViolationTime = 0.0;
		
		int numFails = 0;
		int numUnspecified = 0;
		int total = 0;
		int minFrequency = 0;
		
		for (Route route : engine.getRoutes()) {
			RouteTiming routeTiming = route.getRouteTiming();
				
			if (routeTiming.isTimingSpecified()) {			
				if (routeTiming.passedTiming()) {
					int frequency = routeTiming.getMinFrequency();
					if (frequency > minFrequency) {
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
				console.display(String.format("Pass, Min Frequency: %d Mhz\n", 
			    		minFrequency));			
			}
		} else {
		    console.display("Fail, Timing Unspecified\n");
		}
    }
}
