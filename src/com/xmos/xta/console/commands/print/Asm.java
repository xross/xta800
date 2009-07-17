package com.xmos.xta.console.commands.print;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.routes.Route;

public class Asm extends Command {
	private int m_routeId;
	private int m_nodeId;
	private boolean m_nodeIdSet;

	public Asm() {
		m_routeId = 0;
		m_nodeId = 0;
		m_nodeIdSet = false;
	}

	public Asm(int routeId, int nodeId, boolean nodeIdSpecified) {
		m_routeId = routeId;
		m_nodeId = nodeId;
		m_nodeIdSet = nodeIdSpecified;
}
	
    public int numArgs() {
    	if (m_nodeIdSet) {
    		return 2;
    	}
    	return 1;
    }        

    public String name() {
    	return "asm";
    }        

    public String commandLine() {
    	if (m_nodeIdSet) {
        	return "asm " + getRouteId(m_routeId) + " " + m_nodeId;
    	}
    	return "asm " + getRouteId(m_routeId);
    }
    
    public String description(Engine engine, boolean detailed) {
        return "asm <route id> [node id]: Display annotated assembly for the given route/node";      	
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) 
			return null;		

    	int routeId = 0;
    	try {
    		routeId = getRouteId(args[index + 1]);
	    } catch (NumberFormatException e) { 
	    	return null; 
	    }
	    
		if (!areArgsDone(args, index + 2)) {
	    	try {
	    		return new Asm(routeId, getNodeId(args[index + 2]), true);
		    } catch (NumberFormatException e) { 
		    	return null; 
		    }
		}
		return new Asm(routeId, 0, false);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

	    if (!validateRouteId(engine, console, m_routeId))
		    return;
		
	    if (m_nodeIdSet && !validateNodeId(engine, console, m_routeId, m_nodeId))
		    return;	    	
	    
		AsmDisplayer displayer = new AsmDisplayer(engine, console);
		for (Route route : engine.getRoute(m_routeId)) {
			if (m_nodeIdSet) {
				displayer.display(route.getNode(m_nodeId));						
			} else {
				displayer.display(route.getNode());			
			}        			
		}		
   } 
}
