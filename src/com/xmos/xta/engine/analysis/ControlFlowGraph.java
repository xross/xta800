package com.xmos.xta.engine.analysis;

import java.util.Collection;

import com.xmos.xta.engine.analysis.nodes.FunctionNode;
import com.xmos.xta.engine.analysis.nodes.Node;

public class ControlFlowGraph {
	private Collection<Node> m_threadStartInstructions;
	private Collection<Node> m_functionStartInstructions;
	private Collection<FunctionNode> m_allFunctions;
	
	public ControlFlowGraph(Collection<Node> threadStarts,  
			Collection<Node> functionStarts, Collection<FunctionNode> allFunctions) {
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
		
	public Collection<FunctionNode> getAllFunctions(){
		return m_allFunctions;
	}
}
