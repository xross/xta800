package com.xmos.xta.engine;

public class NextPcType {

	private boolean m_isBranch; 
	private int m_nextPc;
	
	//private int branchRegister;
	//private boolean branchIfTrue;
	//private boolean branchIfFalse;
	
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
