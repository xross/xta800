package com.xmos.xta.engine.analysis;

import java.util.Collection;

import com.xmos.xta.engine.analysis.nodes.Node;

public class ControlFlowGraph {
	private Collection<Node> m_threadStartInstructions;
	private Collection<Node> m_threadEndInstructions;
	private Collection<Node> m_functionStartInstructions;
	
	private Node m_startOfMemory;
	private int m_node;
	private int m_core;
	
	public ControlFlowGraph(Collection<Node> threadStarts, Collection<Node> threadEnds, 
			Collection<Node> functionStarts, Node startOfMemory, int node, int core){
		m_node = node;
		m_core = core;
		m_functionStartInstructions = functionStarts;
		m_threadStartInstructions = threadStarts;
		m_threadEndInstructions = threadEnds;
		m_startOfMemory = startOfMemory;
	}
	
	public Collection<Node> getThreadStartInstructions(){
		return m_threadStartInstructions;
	}
	
	public Collection<Node> getThreadEndInstructions(){
		return m_threadEndInstructions;
	}
	
	public int getNode(){
		return m_node;
	}
	
	public int getCore(){
		return m_core;
	}

	public Node getStartOfMemory() {
		return m_startOfMemory;
	}
	
}
