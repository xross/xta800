package com.xmos.xta.engine;

public class NextPcType {
	private boolean m_isBranch; 
	private int m_nextPc;
	
	public NextPcType(int pc, boolean isBranch) {
		super();
		m_nextPc = pc;
		m_isBranch = isBranch;
	}
	
	public int getPc() {
		return m_nextPc;
	}
	public boolean isBranch() {
		return m_isBranch;
	}

}
