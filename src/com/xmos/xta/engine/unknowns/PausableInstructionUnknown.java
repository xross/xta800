package com.xmos.xta.engine.unknowns;

import com.xmos.xta.engine.XtaInstruction;

public class PausableInstructionUnknown extends Unknown {		

	private XtaInstruction m_instruction;
	
	public PausableInstructionUnknown(XtaInstruction instruction) {
		m_instruction = instruction;
	}
	
	public int getPc() {
		return m_instruction.getPc();
	}
	
	public int getBasicBlockId() {
		return m_instruction.getBasicBlockId();
	}

	public String getDescription() {
		if (isUnknown()) {
			return String.format("Time taken for instruction (0x%x: %s) is unknown",
					m_instruction.getPc(),
					m_instruction.getNmenonic());
		} else {
			return String.format("Time for instruction (0x%x: %s) set to %sns",
					m_instruction.getPc(),
					m_instruction.getNmenonic(),
					Double.toString(m_instruction.getTimeTaken()));
		}
	}

	public boolean isUnknown() {
		return m_instruction.isUnknown();
	}
	
	public int compareTo(Unknown unknown) {
		if (unknown instanceof PausableInstructionUnknown) {
			if (m_instruction == ((PausableInstructionUnknown)unknown).m_instruction)
				return 0;
			else
				return 1;
		} else {
			return -1;
		}
	}
}
