package com.xmos.xta.console.commands.remove;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;

public class Route extends Command {
	private int m_routeId;
	
	public Route() {
		m_routeId = 0;
	}
	
	public Route(int routeId) {
		m_routeId = routeId;
	}
	
    public int numArgs() { 
    	return 1; 
    }
    
    public String name() { 
    	return "route"; 
    }
    
    public String commandLine() { 
    	return "route " + getRouteId(m_routeId); 
    }        
    
    public String description(Engine engine, boolean detailed) {
    	return "route <route id>: Removes the route with the given id from the current analysis";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) 
			return null;		

    	try {
    		return new Route(getRouteId(args[index + 1])); 
    	}
    	catch (NumberFormatException e) {
    	    return null;
    	}
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

	    if (!validateRouteId(engine, console, m_routeId))
		    return;
    	
    	engine.removeRoute(getSrc(), m_routeId);
    }
}
