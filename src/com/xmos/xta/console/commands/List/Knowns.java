package com.xmos.xta.console.commands.List;

import java.util.HashSet;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Route;
import com.xmos.xta.engine.unknowns.Unknown;

public class Knowns extends Command {
	private int m_routeId;

	public Knowns() {
		m_routeId = 0;
	}

	public Knowns(int routeId) {
		m_routeId = routeId;
	}
	
    public int numArgs() {
    	return 1;
    }

    public String name() {
    	return "knowns";
    }        

    public String commandLine() {
    	return "knowns " + getRouteId(m_routeId);
    }
    
    public String description(Engine engine, boolean detailed) {
        return "knowns <route id>: Displays a list of values which have been set for the given route";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length)
			return null;

    	int routeId;
    	try {
    		routeId = getRouteId(args[index + 1]); 
    	} catch (NumberFormatException e) {
	        return null;
	    }
	    
		return new Knowns(routeId);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

		boolean found = false;
		Route route = engine.getRoute(m_routeId);
		
		if (route != null) {
			HashSet<Unknown> unknowns = route.getUnknowns().getKnowns();		
			for (Unknown unknown : unknowns) {
			    console.display(unknown.getDescription() + "\n");
			    found = true;
			}			
		}
		
		if (!found) {
		    console.display("none\n");
		}
    }    
}
