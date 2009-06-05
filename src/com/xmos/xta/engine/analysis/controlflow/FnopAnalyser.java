package com.xmos.xta.engine.analysis.controlflow;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.instructions.XtaInstruction;
import com.xmos.xta.engine.machine.InstructionBuffer;

public class FnopAnalyser {

	ControlFlowGraph m_cfg;
	
	public FnopAnalyser(ControlFlowGraph cfg) {
		m_cfg = cfg;
	}

	public void analyse() {
		//iterate across all threads and functions and fill in their iBuffer states
		InstructionBuffer ib = new InstructionBuffer();
		for(Node function : m_cfg.getFunctionStartInstructions()){
			
		}
		
		for(Node thread : m_cfg.getThreadStartInstructions()){
			
		}
	}
	
	private void simulateBasicBlock(InstructionBuffer ib, Node block){
		for(XtaInstruction inst : block.getInstructions()){
			
		}
	}
	

}
