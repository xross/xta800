package com.xmos.xta.core.engine.analysis.controlflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class UnreachableCodeFinder {

	private Set<Node> m_unreachable;
		
	public UnreachableCodeFinder() {
		m_unreachable = new TreeSet<Node>();
	}
	
	/**
	 * This finds all the code that is unreachable and removes it from the graph. 
	 * It needs removing from the graph so that functions 
	 * and threads don't have invalid predecessors.
	 */
	public void analyseReachable(InstructionDecoder id, Collection<InstructionNode> threadStarts, 
			Collection<InstructionNode> functionStarts) {
		m_unreachable.clear();
		m_unreachable.addAll(id.getInstructions());
		Set<Node> reachable = new TreeSet<Node>();
		for (Node node : threadStarts){
			Set<Node> seen = new TreeSet<Node>();
			explore(node , seen);
			reachable.addAll(seen);
		}
		for (Node node : functionStarts){
			Set<Node> seen = new TreeSet<Node>();
			explore(node, seen);
			reachable.addAll(seen);
		}
		m_unreachable.removeAll(reachable);
		
		for (Node unreachable : m_unreachable) {
			for (Node next : unreachable.getNextNodes()) {
				next.removePrevNode(unreachable);
			}
			unreachable.clearNextNodes();
		}
	}

	private void explore(Node node, Collection<Node> seen) {
		List<Node> toDo = new ArrayList<Node>();
		toDo.add(node);
		
		while (!toDo.isEmpty()) {
			Node currentNode = toDo.get(0);
			if (!seen.contains(currentNode)) {
				seen.add(currentNode);
				for (Node next : currentNode.getNextNodes()) {
					if (!seen.contains(next)) {
						toDo.add(next);
					}
				}
			}
			
			toDo.remove(currentNode);
		}
	}
}
