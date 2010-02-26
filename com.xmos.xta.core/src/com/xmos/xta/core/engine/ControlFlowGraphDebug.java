package com.xmos.xta.core.engine;

import java.util.HashSet;
import java.util.Set;

import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class ControlFlowGraphDebug {

	private int m_graphCount;
	public ControlFlowGraphDebug() {
		m_graphCount = 0;
	}

	public void dumpDot(Set<InstructionNode> functionStartNodes){
		for(Node start : functionStartNodes){
			dumpGraph(start, "");
		}
	}

	public void dumpGraph(Node start, String functionName){
		Set<Node> seen = new HashSet<Node>();
		explore(start, seen);
		System.out.println("digraph " + functionName + m_graphCount++ + "{");
		for(Node node : seen){
			if(node instanceof InstructionNode){
				System.out.println("node" + node.getId() + " [label=\"" + node.toString() +"\", style=filled, color=green, shape=box];");
			} else {
				System.out.println("node" + node.getId() + " [label=\"" + node.toString() +"\", style=filled, color=red];");
			}
			for(Node next : node.getNextNodes()){
				System.out.println("\t" + "node" + node.getId() + " -> node" + next.getId());
			}
		}
		System.out.println("}");
	}

	private void explore(Node node, Set<Node> seen) {
		seen.add(node);
		for(Node next : node.getNextNodes()){
			if(!seen.contains(next)){
				explore(next, seen);
			}
		}
	}
}
