package com.xmos.xta.console.commands.print;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.routes.Route;
import com.xmos.xta.engine.analysis.routes.RouteTiming;

public class RouteInfo extends Command {
	private int m_routeId;
	
	public RouteInfo() {
		m_routeId = 0;
	}
	
	public RouteInfo(int routeId) {
		m_routeId = routeId;
	}
	
    public int numArgs() {
   		return 1;
    }        

    public String name() {
    	return "route_info";
    }        

    public String commandLine() {
   	    return "route_info " + getRouteId(m_routeId);
    }        
    
    public String description(Engine engine, boolean detailed) {
    	return "route_info <route id>: Show detailed information for the given route";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) 
			return null;		

        try { 
            return new RouteInfo(getRouteId(args[index + 1])); 
        } catch (NumberFormatException e) {        	
          	return null;
        }    		            		
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

	    if (!validateRouteId(engine, console, m_routeId))
		    return;

	    Route route = engine.getRoute(m_routeId);
	    RouteTiming routeTiming = route.getRouteTiming();

    	console.display(String.format("%s %s, Node: %d, Core: %d\n", 
    			route.getDescription(), route.getEndpointsPcsString(),
    			routeTiming.getNodeId(), routeTiming.getCoreId()));    	    	

    	console.display(String.format("Worst Case Timing:  %8.1f ns,%8.2f mhz,%8d thread cycles\n", 
    			routeTiming.getWorstTime(), 
    			getMhz(routeTiming.getWorstTime()),
    			routeTiming.getWorstCycles()));    	
    	
		if (routeTiming.isTimingSpecified()) {  
    	    console.display(String.format("Required Timing:    %8.1f ns,%8.2f mhz,%8d thread cycles\n", 
    		    	routeTiming.getMaxTime(), 
    		    	getMhz(routeTiming.getMaxTime()),
    			    routeTiming.getMaxCycles()));

			if (routeTiming.passedTiming()) {
                console.display(String.format("Passed, Slack:      %8.1f ns,%8.2f mhz,%8d thread cycles\n", 
        		    	routeTiming.getSlackTime(), 
        		    	getMhz(routeTiming.getSlackTime()), 
        			    routeTiming.getSlackCycles()));
    	    } else {
                console.display(String.format("Failed, Violation:  %8.1f ns,%8.2f mhz,%8d thread cycles\n", 
        		    	routeTiming.getViolationTime(), 
        		    	getMhz(routeTiming.getViolationTime()), 
        			    routeTiming.getViolationCycles()));    		
    	    }    	    
    	    console.display(String.format("Min Core Frequency: %8d mhz\n", 
    		    	routeTiming.getMinFrequency())); 
		} else {
			console.display("No Timing Specified\n");			
		}
    }
    
    private double getMhz(double ns) {
    	return (1.0 * 1000) / ns; 
    }
}
