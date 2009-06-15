package com.xmos.xta.console.commands.print;

import java.util.Collection;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.UntypedNodeTraverser;
import com.xmos.xta.engine.analysis.structural.StructureGraph;

public class NodeInfo extends Command {
	private int m_nodeId;
	private Node m_node;
	
	public NodeInfo() {
		m_nodeId = 0;
		m_node = null;
	}
	
	public NodeInfo(int nodeId) {
		m_nodeId = nodeId;
		m_node = null;
	}
	
    public int numArgs() {
   		return 1;
    }        

    public String name() {
    	return "node_info";
    }        

    public String commandLine() {
   	    return "node_info " + m_nodeId;
    }        
    
    public String description(Engine engine, boolean detailed) {
    	return "node_info <node id>: Show detailed information for the given node";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) 
			return null;		

        try { 
            return new NodeInfo(Integer.parseInt(args[index + 1])); 
        } catch (NumberFormatException e) {        	
          	return null;
        }    		            				
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

	    displayNodeSummary(engine, console);
    }
    
    private void displayNodeSummary(Engine engine, Console console) {
    	Node node = getNode(engine, m_nodeId);
    	if (node == null) {
    		console.display(String.format("Error: Node id: %d nt found\n", m_nodeId));
    		return;
    	}    	
    	console.display(node.getAttributes().getTimingDetails());
    }
    
    private Node getNode(Engine engine, final int nodeId) {
		StructureGraph structureGraph = engine.getStructureGraph();

		Collection<Node> nodes = structureGraph.getThreads();	
		for (Node node : nodes) {					
		    node.traverse(0, new UntypedNodeTraverser() {
				public boolean foundNode(Node node, int depth) {
					if (node.getId() == nodeId) {
						m_node = node;
						return false;
					}
					return true;
				}
		    });
		}
        return m_node;   	
    }
}
