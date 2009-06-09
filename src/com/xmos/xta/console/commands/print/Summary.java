package com.xmos.xta.console.commands.print;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.routes.Route;
import com.xmos.xta.engine.analysis.routes.RouteTiming;

public class Summary extends Command {
    private int m_verbosity;
    private boolean m_verbositySpecified;
	
	public Summary() {
	}

	public Summary(int verbosity, boolean verbositySpecified) {		
		m_verbosity = verbosity;
		m_verbositySpecified = verbositySpecified;
	}
	
    public int numArgs() { 
    	if (m_verbositySpecified) { 
    		return 1;
        }
    	return 0; 
    }
    
    public String name()        { return "summary"; }        
    public String commandLine() { return "summary"; }        
    
    public String description(Engine engine, boolean detailed) {
    	return "summary: Show routes summary [verbosity 0|1|2|3(default)]";
    }

    public Command clone(String[] args, int index) {
		int verbosity = 2;
		boolean verbositySpecified = false;
    	if (!areArgsDone(args, index + 1)) {
        	try {
        		verbosity = Integer.parseInt(args[index + 1]); 
        		if ((verbosity < 0) || (verbosity > 3))
        			return null;

        	} catch (NumberFormatException e) {
    	        return null;
    	    }
        	verbositySpecified = true;
    	}    	
   		return new Summary(verbosity, verbositySpecified);
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
		switch (m_verbosity) {
		case 0:
			break;
			
		case 1:
			displayRouteSummary(console, route, true, false);			
			break;

		case 2:
			displayRouteSummary(console, route, true, true);			
			break;
						
		default:
			assert(false);
		    break;
		}  	
    }
    
	private void displayRouteSummary(Console console, Route route, 
			boolean displayFailures, boolean displayPasses) {

		
		RouteTiming routeTiming = route.getRouteTiming();				
		if (routeTiming.isTimingSpecified()) {			
			if (routeTiming.passedTiming()) {
				if (displayPasses) {
					console.display(String.format("Route(%d) %s\n%s\n", 
							route.getRouteId(), 
							route.getDescription(),
							routeTiming.getDescription(true))); 
				}
			} else {
				if (displayFailures) {
					console.display(String.format("Route(%d) %s\n%s\n", 
							route.getRouteId(), 
							route.getDescription(),
							routeTiming.getDescription(true))); 
				}
			}			
		} else {
			console.display(String.format("Route(%d) %s\n%s\n", 
					route.getRouteId(), 
					route.getDescription(),
			        routeTiming.getDescription(false)));
		}
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
