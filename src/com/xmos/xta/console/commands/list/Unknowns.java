package com.xmos.xta.console.commands.list;

import java.util.HashSet;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.routes.Route;
import com.xmos.xta.engine.unknowns.Unknown;

public class Unknowns extends Command {
	private int m_routeId;

	public Unknowns() {
	}

	public Unknowns(int routeId) {
		m_routeId = routeId;
	}

    public int numArgs() {
    	return 1;
    }        

    public String name() {
    	return "unknowns";
    }        

    public String commandLine() {
    	return "unknowns " + getRouteId(m_routeId);
    }        
    
    public String description(Engine engine, boolean detailed) {
    	String descString = String.format("unknowns <route id>: Displays a list of the unknowns for the given route");
    	return descString;
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
	    
		return new Unknowns(routeId);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

		boolean found = false;
		Route route = engine.getRoute(m_routeId);
		
		if (route != null) {
			HashSet<Unknown> unknowns = route.getUnknowns().getUnknowns();		
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
