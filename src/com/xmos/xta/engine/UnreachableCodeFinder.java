package com.xmos.xta.engine;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.xmos.xta.engine.analysis.nodes.Node;

public class UnreachableCodeFinder {

	private Set<Node> m_unreachable;
	private InstructionDecoder m_id;
	private Collection<Node> m_threadStarts;
	private Collection<Node> m_functionStarts;
		
	public UnreachableCodeFinder(InstructionDecoder id, Collection<Node> threadStarts, Collection<Node> functionStarts) {
		m_id = id;
		m_threadStarts = threadStarts;
		m_functionStarts = functionStarts;
	}
	
	public Collection<Node> getUnreachableCode(){
		return m_unreachable;
	}
	
	/**
	 * This finds all the code that is unreachable and removes it from the graph. 
	 * It needs removing from the graph so that functions 
	 * and threads don't have invalid predecessors.
	 */
	public void analyseReachable(){
		m_unreachable = new HashSet<Node>(m_id.getInstructions());
		Set<Node> reachable = new HashSet<Node>();
		for (Node node : m_threadStarts){
			Set<Node> seen = new HashSet<Node>();
			explore(node , seen);
			reachable.addAll(seen);
		}
		for (Node node : m_functionStarts){
			Set<Node> seen = new HashSet<Node>();
			explore(node, seen);
			reachable.addAll(seen);
		}
		m_unreachable.removeAll(reachable);
		
		for (Node unreachable : m_unreachable) {
			for (Node next : unreachable.getNextNodes()) {
				next.removePrevNode(unreachable);
			}
			unreachable.getNextNodes().clear();
		}
	}

	private void explore(Node node, Collection<Node> seen){
		seen.add(node);
		for (Node next : node.getNextNodes()) {
			if (!seen.contains(next)) {
				explore(next, seen);
			}
		}
	}
	
}
