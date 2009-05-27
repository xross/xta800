package com.xmos.xta.engine.analysis;

import java.util.Collection;

import com.xmos.xta.engine.analysis.nodes.Function;
import com.xmos.xta.engine.analysis.nodes.Node;

public class ControlFlowGraph {
	private Collection<Node> m_threadStartInstructions;
	private Collection<Node> m_functionStartInstructions;
	private Collection<Function> m_allFunctions;
	private Node m_startOfMemory;
	private int m_node;
	private int m_core;
	
	public ControlFlowGraph(Collection<Node> threadStarts,  
			Collection<Node> functionStarts, Collection<Function> allFunctions, int node, int core){
		m_node = node;
		m_core = core;
		m_functionStartInstructions = functionStarts;
		m_threadStartInstructions = threadStarts;
		m_allFunctions = allFunctions;
	}
	
	public Collection<Node> getFunctionStartInstructions(){
		return m_functionStartInstructions;
	}
	
	public Collection<Node> getThreadStartInstructions(){
		return m_threadStartInstructions;
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
	
	public Collection<Function> getAllFunctions(){
		return m_allFunctions;
	}
	
}
