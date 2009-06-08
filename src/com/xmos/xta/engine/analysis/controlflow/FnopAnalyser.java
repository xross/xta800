package com.xmos.xta.engine.analysis.controlflow;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.instructions.XtaInstruction;

public class FnopAnalyser {

	ControlFlowGraph m_cfg;
	
	public FnopAnalyser(ControlFlowGraph cfg) {
		m_cfg = cfg;
	}

	public void analyse() {
		//iterate across all threads and functions and fill in their iBuffer states
		for(Node function : m_cfg.getFunctionStartInstructions()){
			InstructionBuffer ib = new InstructionBuffer();
			ib.setPc(function.getStartPc());
			ib.fetch();
			function.getStartInstructionBuffers().add(ib);
		}
		
		for(Node thread : m_cfg.getThreadStartInstructions()){
			InstructionBuffer ib = new InstructionBuffer();
			ib.setPc(thread.getStartPc());
			thread.getStartInstructionBuffers().add(ib);
		}
	}
	
	private void simulateBasicBlock(InstructionBuffer ib, Node block){
		for(XtaInstruction inst : block.getInstructions()){
			
		}
	}
	

}
