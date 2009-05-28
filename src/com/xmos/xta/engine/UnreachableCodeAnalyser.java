package com.xmos.xta.engine;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.xmos.xta.engine.analysis.nodes.Node;
import com.xmos.xta.engine.analysis.patterns.CommonMethods;

public class UnreachableCodeAnalyser {

	private Set<Node> m_unreachable;
	private InstructionDecoder m_id;
	private Collection<Node> m_threadStarts;
	private Collection<Node> m_functionStarts;
	
	//TODO - this currently doesn't detect unreachable functions
	
	public UnreachableCodeAnalyser(InstructionDecoder id, Collection<Node> threadStarts, Collection<Node> functionStarts) {
		m_id = id;
		m_threadStarts = threadStarts;
		m_functionStarts = functionStarts;
	}
	
	public Collection<Node> getUnreachableCode(){
		return m_unreachable;
	}
	
	/**
	 * This find all the code that is unreachable and removes it from the graph. It need removing from the graph so that functions 
	 * and threads don't have invalid predecessors.
	 */
	public boolean analyseReachable(){
		m_unreachable = new HashSet<Node>(m_id.getInstructions());
		Set<Node> reachable = new HashSet<Node>();
		for(Node s: m_threadStarts){
			Set<Node> seen = new HashSet<Node>();
			explore(s, seen);
			reachable.addAll(seen);
		}
		for(Node s: m_functionStarts){
			Set<Node> seen = new HashSet<Node>();
			explore(s, seen);
			reachable.addAll(seen);
		}
		m_unreachable.removeAll(reachable);
		
		for(Node unreachable : m_unreachable){
			for(Node next : unreachable.getNextNodes()){
				CommonMethods.disconnect(next, unreachable);
			}
			for(Node prev : unreachable.getNextNodes()){
				CommonMethods.disconnect(unreachable, prev);
			}
		}
		
		return true;
	}

	private void explore(Node node, Collection<Node> seen){
		seen.add(node);
		for(Node next : node.getNextNodes()){
			if(!seen.contains(next)){
				explore(next, seen);
			}
		}
	}
	
}
