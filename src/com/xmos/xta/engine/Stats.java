package com.xmos.xta.engine;

import java.util.Collection;
import com.xmos.xta.console.Console;
import com.xmos.xta.engine.analysis.StructureGraph;
import com.xmos.xta.engine.analysis.nodes.Node;
import com.xmos.xta.engine.analysis.nodes.NodeTraverser;

public class Stats {

	
	public void display(StructureGraph structureGraph) {

		Collection<Node> nodes = structureGraph.getThreads();	
		for (Node node : nodes) {					
		    node.traverse(0, new NodeTraverser() {
				public void foundBlockNode( Node node, int depth) {
					backTrace(node);					
				}

				public void foundFunctionNode(Node node, int depth) {
					backTrace(node);
				}

				public void foundInstructionNode(Node node, int depth) {
					backTrace(node);			
				}

				public void foundLoopNode(Node node, int depth) {
					backTrace(node);		
				}

				public void foundPathNode(Node node, int depth) {
					backTrace(node);					
				}

				public void foundSectionNode(Node node, int depth) {
					backTrace(node);	
				}

				public void foundTerminalNode(Node node, int depth) {
					backTrace(node);				
				}	
				
				private void backTrace(Node node) {
					Node currentNode = node;
					String trace = "";
					while(currentNode.getPrevNodes().size() == 1){
						trace += currentNode.getGraphvizName();
						currentNode = currentNode.getPrevNodes().iterator().next();
					}
					
					System.out.println(trace);
				}
		    });
		System.out.println();    
		}		
		
    }	
	
	
}
