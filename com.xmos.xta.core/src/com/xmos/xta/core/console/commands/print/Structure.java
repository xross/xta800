package com.xmos.xta.core.console.commands.print;

import java.util.Collection;
import java.util.TreeSet;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class Structure extends Command {
	
	private int m_routeId;
	private int m_nodeId;
	private boolean m_nodeIdSet;
	
	public Structure() {
		m_routeId = 0;
		m_nodeId = 0;
		m_nodeIdSet = false;
	}

	public Structure(int routeId, int nodeId, boolean nodeIdSpecified) {
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
    	return "structure";
    }        

    public String commandLine() {
       	if (m_nodeIdSet) {
           	return "structure " + getRouteId(m_routeId) + " " + m_nodeId;
       	}
   		return "structure " + getRouteId(m_routeId);
    }
    
    public String description(Engine engine, boolean detailed) {
    	String descString = "structure <route id> [node id]: Display the structure for given route/node (verbosity 0|1)";
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
	    		return new Structure(routeId, getNodeId(args[index + 2]), true);
		    } catch (NumberFormatException e) { 
		    	return null; 
		    }
		}
		return new Structure(routeId, 0, false);
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

	    if (!validateRouteId(engine, console, m_routeId))
		    return;

	    if (m_nodeIdSet && !validateNodeId(engine, console, m_routeId, m_nodeId))
		    return;	    	

		StructureDisplayer displayer = new StructureDisplayer(console, engine.getVerbosity());
	    
		Collection<Node> nodes = null;
		if (m_routeId == Engine.ROUTE_ID_GLOBAL) {
    		if (m_nodeIdSet) {
    			nodes = getCollection(engine.getStructureGraph().getNode(m_nodeId));
    		} else {
    			nodes = new TreeSet<Node>();
    			nodes.addAll(engine.getStructureGraph().getThreads());    			
    		}			
    		displayer.display(nodes);				
		
    		
		} else {
			for (Route route : engine.getRoute(m_routeId)) {
	    		if (m_nodeIdSet) {
		    		nodes = getCollection(route.getNode(m_nodeId));			
			    } else {
				    nodes = getCollection(route.getNode());
			    }				
	    		displayer.display(nodes);
			}
		}		
    }
   
}
