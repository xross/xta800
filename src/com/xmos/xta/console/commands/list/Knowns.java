package com.xmos.xta.console.commands.list;

import java.util.Collection;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes.UnknownState;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.UntypedNodeTraverser;

public class Knowns extends Command {
	public enum Mode {
		GLOBAL,
		ROUTE,
	}
	private Mode m_mode;
	private int m_routeId;
	private boolean m_found;

	public Knowns() {
		m_mode = Mode.GLOBAL;
		m_found = false;
	}

	public Knowns(Mode mode, int routeId) {
		m_mode = mode;
		m_routeId = routeId;
		m_found = false;
	}
	
    public int numArgs() {
    	switch (m_mode) {
    	case GLOBAL:
    		return 0;
    	case ROUTE:
    		return 1;
    	}
		assert(false);
	    return 0;
    }

    public String name() {
    	return "knowns";
    }        

    public String commandLine() {
    	switch (m_mode) {
    	case GLOBAL:
    		return "knowns";
    	case ROUTE:
    		return "knowns " + getRouteId(m_routeId);
    	}
		assert(false);
	    return "";    	
    }
    
    public String description(Engine engine, boolean detailed) {
        return "knowns [route id]: Displays the list of unknowns set for the given route/global tree";
    }

    public Command clone(String[] args, int index) {
		if (!areArgsDone(args, index + 1)) {
	    	try {
	    	    return new Knowns(Mode.ROUTE, getRouteId(args[index + 1]));
		    } catch (NumberFormatException e) {
		        return null;
		    }    	
		}
    	return new Knowns(Mode.GLOBAL, 0);		
    }
    
    public void execute(Shell shell, Engine engine, final Console console) {
		if (!isExecutableLoaded(engine, console))
			return;
		
	    Collection<Node> nodes = null;
	    switch (m_mode) {
	    case GLOBAL:
	    	nodes = engine.getStructureGraph().getThreads();
	    	break;
	    	
	    case ROUTE:
	    	nodes = engine.getRoute(m_routeId).getSubRoutes();
		    if (!validateRouteId(engine, console, m_routeId))
			    return;
	    	break;	    	
	    }
		
		for (Node node : nodes) {
		    node.traverse(0, new UntypedNodeTraverser() {
		    	public boolean foundNode(Node node, int depth) {
		    		NodeAttributes nodeAttributes = node.getAttributes();		    		
		    		if (nodeAttributes.isUnknown() &&
		    			(nodeAttributes.getUnknownState() == UnknownState.KNOWN)) {
		    			console.display(nodeAttributes.getUnknownDescription() + "\n");
		    			m_found = true;
		    		}		    			
		        	return true;
		    	}
		    });
		}		
		if (!m_found) {
		    console.display("none\n");
		}
    }   
}
