package com.xmos.xta.engine.xtagraph.nodes;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.xmos.xta.engine.BasicBlock;
import com.xmos.xta.engine.XtaInstruction;

public class Block extends Node {

	private BasicBlock m_block;
	private List<XtaInstruction> m_fnops; 
	private List<XtaInstruction> m_instructionStream;
	
	
	public Block(BasicBlock block){
		super();
		m_block = block;
		m_instructionStream = new LinkedList<XtaInstruction>();
	}
	
	public List<XtaInstruction> getInstructionStream() {
		return m_instructionStream;
	}

	public List<XtaInstruction> getInstructions() {
		return m_block.getInstructions();
	}

	public long getNumberCycles() {
		long cycles = 0;
		for(XtaInstruction inst : getInstructionStream()){
			cycles += inst.getNumCycles();
		}
		return cycles;
	}

	public long getNumberFNOPS() {
		return m_fnops.size();
	}

	public long getNumberInstructions() {
		return m_block.getInstructions().size() + m_fnops.size();
	}

	public Set<Integer> getPCsCovered() {
		Set<Integer> pcs = new HashSet<Integer>();
		for(XtaInstruction inst : m_block.getInstructions()){
			pcs.add(inst.getPc());
		}
		return pcs;
	}

}
