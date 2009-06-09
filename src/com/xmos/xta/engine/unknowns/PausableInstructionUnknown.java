package com.xmos.xta.engine.unknowns;

import java.util.LinkedList;
import java.util.Set;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.instructions.XtaInstruction;

public class PausableInstructionUnknown extends Unknown {		

	private Node m_instruction;
	
	public PausableInstructionUnknown(Node instruction) {
		m_instruction = instruction;
	}
	
	public int getPc() {
		Set<Integer> pcs = m_instruction.getPCs();
		assert(pcs.size() == 1);
		return pcs.iterator().next();
	}
	
	public int getNodeId() {
		return m_instruction.getId();
	}

	public String getDescription() {
		if (isUnknown()) {
			return String.format("Time taken for instruction (0x%x: %s) is unknown",
					getPc(),
					getNmenonic());
		} else {
			return String.format("Time for instruction (0x%x: %s) set to %sns",
					getPc(),
					getNmenonic(),
					Double.toString(m_instruction.getInstructionTimeTaken()));
		}
	}

	public boolean isUnknown() {
		return m_instruction.isInstructionUnknown();
	}

	public int compareTo(Unknown unknown) {
		if (unknown instanceof PausableInstructionUnknown) {
			if (m_instruction == ((PausableInstructionUnknown) unknown).m_instruction)
				return 0;
			else
				return 1;
		} else {
			return -1;
		}
	}
	
	private String getNmenonic() {
		LinkedList<XtaInstruction> instructions = m_instruction.getInstructions();
		assert(instructions.size() == 1);
		return instructions.iterator().next().getNmenonic();
	}
}
