package com.xmos.xta.core.console.commands.debug;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.utils.TimeUnit;

public class InstructionTime extends Command {
	private static final String[] COMMAND_NAMES = { "instructiontime" };

	private int m_routeId;
	private String m_nodeIdString;

	public InstructionTime() {
		super(COMMAND_NAMES);
		m_routeId = 0;
		m_nodeIdString = "";
    }

	public InstructionTime(int routeId, String nodeIdString) {
		super(COMMAND_NAMES);
		m_routeId = routeId;
		m_nodeIdString = nodeIdString;
	}

    public int numArgs() {
    	return 2;
    }

    public String commandLine() {
    	return name() + " " + getRouteId(m_routeId) + " " + m_nodeIdString;
    }

    public String description(Engine engine, boolean detailed) {
        return name() + " <route id> <node id>: Display the instruction time set for the given node in the given route";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length)
			return null;

        try {
            return new InstructionTime(getRouteId(args[index + 1]), args[index + 2]);
        } catch (NumberFormatException e) {
          	return null;
        }
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

		Node node = getReferencedNode(engine, m_routeId, m_nodeIdString);
		if (node != null) {
	    	display(engine, console, node);

		} else {
	        try {
	        	int nodeId = getNodeId(m_nodeIdString);
				if (!validateNodeId(engine, console, m_routeId, nodeId))
				    return;
	        	
		    	display(engine, console, engine.getSelectedRoute().getNode(nodeId));
	        	
	        } catch (NumberFormatException e) {
	          	return;
	        }			
		}		
    }

    private void display(Engine engine, Console console, Node node) {
    	if ((node != null) && (node instanceof InstructionNode)) {	    		
			engine.setSelectedRouteId(getSrc(), m_routeId);
    	    TimeUnit timeUnit = engine.getInstructionTime(node);	 
        	console.display(timeUnit.getDescription(true) + "\n");
    	}    	    	
    }
}
