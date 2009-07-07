package com.xmos.xta.console.commands.print;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.routes.Route;
import com.xmos.xta.engine.analysis.routes.RouteTiming;
import com.xmos.xta.engine.analysis.routes.RouteTiming.Level;

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

    	console.display(String.format("%s %s\n", route.getDescription(), route.getEndpointsPcsString()));    	
    	console.display(routeTiming.getDescription(Level.COMPLETE));    	
    }
}
