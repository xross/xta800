package com.xmos.xta.engine.unknowns;

import com.xmos.xta.engine.XtaInstruction;

public class InstructionUnknown extends Unknown {		

	private XtaInstruction m_instruction;
	
	public InstructionUnknown(XtaInstruction instruction) {
		m_instruction = instruction;
	}
	
	@Override
	public int getPc() {
		return m_instruction.getPc();
	}
	
	@Override
	public int getBasicBlockId() {
		return m_instruction.getBasicBlockId();
	}

	@Override
	public String getDescription() {
		if (isUnknown()) {
			return String.format("Time taken for instruction (0x%x: %s) is unknown\n",
					m_instruction.getPc(),
					m_instruction.getNmenonic());
		} else {
			return String.format("Time for instruction (0x%x: %s) set to %sns\n",
					m_instruction.getPc(),
					m_instruction.getNmenonic(),
					Double.toString(m_instruction.getTimeTaken()));
		}
	}

	@Override
	public boolean isUnknown() {
		return m_instruction.isUnknown();
	}
	
	public int compareTo(Unknown unknown) {
		if (unknown instanceof InstructionUnknown) {
			if (m_instruction == ((InstructionUnknown)unknown).m_instruction)
				return 0;
			else
				return 1;
		} else {
			return -1;
		}
	}
}
