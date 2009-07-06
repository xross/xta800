package com.xmos.xta.console.commands.print;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;

public class Trace extends Command {
	private int m_routeId;
	private int m_nodeId;
	private boolean m_nodeIdSet;

	public Trace() {
		m_routeId = 0;
		m_nodeId = 0;
		m_nodeIdSet = false;
	}
	
	public Trace(int routeId, int nodeId, boolean nodeIdSpecified) {
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
    	return "trace";
    }        

    public String commandLine() {
    	if (m_nodeIdSet) {
        	return "trace " + getRouteId(m_routeId) + " " + m_nodeId;
    	}
    	return "trace " + getRouteId(m_routeId);
    }
    
    public String description(Engine engine, boolean detailed) {
    	String descString = String.format("trace <route id> [node id]: Display instruction trace for the worst case path of the given route/node");
    	return descString;    	                
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
	    		return new Trace(routeId, getNodeId(args[index + 2]), true);
		    } catch (NumberFormatException e) { 
		    	return null; 
		    }
		}
		return new Trace(routeId, 0, false);
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

	    if (!validateRouteId(engine, console, m_routeId))
		    return;

	    if (m_nodeIdSet && !validateNodeId(engine, console, m_routeId, m_nodeId))
		    return;	    	

		TraceDisplayer displayer = new TraceDisplayer(console);		
		if (m_nodeIdSet) {
			displayer.display(engine.getRoute(m_routeId).getNode(m_nodeId), engine.getCase());						
		} else {
			displayer.display(engine.getRoute(m_routeId).getNode(), engine.getCase());			
		}
    }    
}
