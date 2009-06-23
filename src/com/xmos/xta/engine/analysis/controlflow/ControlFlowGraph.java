package com.xmos.xta.engine.analysis.controlflow;

import java.util.Collection;
import com.xmos.xta.engine.analysis.structural.nodes.FunctionNode;
import com.xmos.xta.engine.analysis.structural.nodes.InstructionNode;

public class ControlFlowGraph {
	private Collection<InstructionNode> m_threadStartInstructions;
	private Collection<InstructionNode> m_functionStartInstructions;
	private Collection<FunctionNode> m_allFunctions;
	
	public ControlFlowGraph(Collection<InstructionNode> threadStarts,  
			Collection<InstructionNode> functionStarts, Collection<FunctionNode> allFunctions) {
		m_functionStartInstructions = functionStarts;
		m_threadStartInstructions = threadStarts;
		m_allFunctions = allFunctions;
	}
	
	public Collection<InstructionNode> getFunctionStartInstructions(){
		return m_functionStartInstructions;
	}
	
	public Collection<InstructionNode> getThreadStartInstructions(){
		return m_threadStartInstructions;
	}
		
	public Collection<FunctionNode> getAllFunctions(){
		return m_allFunctions;
	}
}
