package com.xmos.xta.console.commands.Print;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.RouteTiming;

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
   	    return "route_info " + m_routeId;
    }        
    
    public String description(Engine engine, boolean detailed) {
    	return "route_info <route id>: Show detailed information for the given route";
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
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
    	
	    com.xmos.xta.engine.Route route = engine.getRoute(m_routeId);
	    RouteTiming routeTiming = route.getRouteTiming();
	    
    	console.display(String.format("%s, Node: %d, Core: %d\n", 
    			route.getDescription(), routeTiming.getNodeId(), routeTiming.getCoreId()));
    	
    	if (routeTiming.passedTiming()) {
        	console.display(String.format("Pass, Slack: %.1fns/%d thread cycles\n", 
        			routeTiming.getSlackTime(), routeTiming.getSlackCycles()));
    	} else {
        	console.display(String.format("Fail, Violation: %.1fns/%d thread cycles\n", 
        			routeTiming.getViolationTime(), routeTiming.getViolationCycles()));    		
    	}
    	console.display(String.format("Required Timing: %.1fns/%d thread cycles\n", 
    			routeTiming.getMaxTime(), routeTiming.getMaxCycles()));    		    	
    	console.display(String.format("Worst Case Timing: %.1fns/%d thread cycles\n", 
    			routeTiming.getWorstTime(), routeTiming.getWorstCycles()));    		    	
    	console.display(String.format("Minimum Frequency: %d Mhz\n", 
    			routeTiming.getMinFrequency()));    		    	
    }    
}
