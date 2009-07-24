package com.xmos.xta.engine.analysis.controlflow;

import java.util.Collection;
import com.xmos.xta.engine.analysis.structural.nodes.InstructionNode;

public class ControlFlowGraph {
	private Collection<InstructionNode> m_threadStartInstructions;
	private Collection<InstructionNode> m_functionStartInstructions;
	
	public ControlFlowGraph(Collection<InstructionNode> threadStarts,  
			Collection<InstructionNode> functionStarts) {
		m_functionStartInstructions = functionStarts;
		m_threadStartInstructions = threadStarts;
	}
	
	public Collection<InstructionNode> getFunctionStartInstructions(){
		return m_functionStartInstructions;
	}
	
	public Collection<InstructionNode> getThreadStartInstructions(){
		return m_threadStartInstructions;
	}
		
}
