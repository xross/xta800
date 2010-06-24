package com.xmos.xta.core.console.commands.print;

import java.util.Collection;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class Structure extends Command {
	private static final String[] COMMAND_NAMES = { "structure" };

	private int m_routeId;
	private int m_nodeId;
	private boolean m_nodeIdSet;

	public Structure() {
		super(COMMAND_NAMES);
		m_routeId = 0;
		m_nodeId = 0;
		m_nodeIdSet = false;
	}

	public Structure(int routeId, int nodeId, boolean nodeIdSpecified) {
		super(COMMAND_NAMES);
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

    public String commandLine() {
       	if (m_nodeIdSet) {
           	return name() + " " + getRouteId(m_routeId) + " " + m_nodeId;
       	}
   		return name() + " " + getRouteId(m_routeId);
    }

    public String description(Engine engine, boolean detailed) {
    	return name() + " <route id> [node id]: Display the structure for given route/node (verbosity 0|1)";
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

		displayRouteTree(engine, console);
    }
    
    private void displayRouteTree(final Engine engine, final Console console) {
		StructureDisplayer displayer = new StructureDisplayer(console, engine.getVerbosity());
		
		Collection<Node> nodes = null;
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
