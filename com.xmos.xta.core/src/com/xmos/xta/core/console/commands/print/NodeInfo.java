package com.xmos.xta.core.console.commands.print;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class NodeInfo extends Command {
	private static final String[] COMMAND_NAMES = { "nodeinfo" };

	private int m_routeId;
	private int m_nodeId;

	public NodeInfo() {
		super(COMMAND_NAMES);
		m_routeId = 0;
		m_nodeId = 0;
	}

	public NodeInfo(int routeId, int nodeId) {
		super(COMMAND_NAMES);
		m_routeId = routeId;
		m_nodeId = nodeId;
	}

    public int numArgs() {
   		return 2;
    }

    public String commandLine() {
   	    return name() + " " + getRouteId(m_routeId) + " " + getNodeId(m_nodeId);
    }

    public String description(Engine engine, boolean detailed) {
    	return name() + " <route id> <node id>: Show detailed information for the given node in the given route";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length)
			return null;

        try {
            return new NodeInfo(getRouteId(args[index + 1]), getNodeId(args[index + 2]));
        } catch (NumberFormatException e) {
          	return null;
        }
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

	    if (!validateNodeId(engine, console, m_routeId, m_nodeId))
		    return;

	    displayNodeSummary(engine, console);
    }

    private void displayNodeSummary(Engine engine, Console console) {
		for (Route route : engine.getRoute(m_routeId)) {
	    	Node node = route.getNode(m_nodeId);
	    	if (node != null) {
	        	console.display(node.getAttributes().getTimingDetails());
	    		return;
	    	}
		}
		engine.notify(getSrc(), Notifier.NOTIFY_ERROR,
				String.format("node id: %d not found", m_nodeId));
    }
}
