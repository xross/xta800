package com.xmos.xta.engine.unknowns;

import com.xmos.xta.engine.PathBasicBlock;

public class LoopUnknown extends Unknown {		

	private PathBasicBlock m_header;
	private PathBasicBlock m_tail;
	private long m_iterations;
	
	public LoopUnknown(PathBasicBlock header, PathBasicBlock tail) {
		m_header = header;
		m_tail = tail;
	}
	
	public int getPc() {
		return m_tail.getInstructionStream().getLast().getPc();
	}
	
	public int getBasicBlockId() {
		return m_tail.getBasicBlock().getId();
	}
	
	public String getDescription() {
		if (isUnknown()) {
			return String.format("Loop iterations on loop path " + m_tail.getPathId() + " is  unknown\n");
		} else {
			return String.format("Loop iterations on loop path " + m_tail.getPathId() + " is set to " +m_iterations + "\n");
		}
	}

	public boolean isUnknown() {
		try {
			m_iterations = m_header.getloops().getLoopIterations(m_tail);
		} catch (Exception e) {
			return true;
		}
		return false;
	}
	
	public int compareTo(Unknown unknown) {
		if (unknown instanceof LoopUnknown) {
			if (m_tail == ((LoopUnknown)unknown).m_tail)
				return 0;
			else
				return 1;
		} else {
			return -1;
		}
	}
}
