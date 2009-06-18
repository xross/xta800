package com.xmos.xta.console.commands.print;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.structural.nodes.Node;

public class NodeInfo extends Command {
	private int m_routeId;
	private int m_nodeId;
	
	public NodeInfo() {
		m_routeId = 0;
		m_nodeId = 0;
	}
	
	public NodeInfo(int routeId, int nodeId) {
		m_routeId = 0;
		m_nodeId = nodeId;
	}
	
    public int numArgs() {
   		return 2;
    }        

    public String name() {
    	return "node_info";
    }        

    public String commandLine() {
   	    return "node_info " + m_routeId + " " + m_nodeId;
    }        
    
    public String description(Engine engine, boolean detailed) {
    	return "node_info <route id> <node id>: Show detailed information for the given node in the given route";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) 
			return null;		

        try { 
            return new NodeInfo(getRouteId(args[index + 1]), Integer.parseInt(args[index + 2])); 
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
    	Node node = engine.getRoute(m_routeId).getNode(m_nodeId);	
    	if (node == null) {
    		console.display(String.format("Error: Node id: %d nt found\n", m_nodeId));
    		return;
    	}    	
    	console.display(node.getAttributes().getTimingDetails());
    }
}
