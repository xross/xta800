package com.xmos.xta.engine;

public class NextPcType {
	private boolean m_isBranch; 
	private XtaInstruction m_nextInstruction;
	
	public NextPcType(XtaInstruction inst, boolean isBranch) {
		super();
		m_nextInstruction = inst;
		m_isBranch = isBranch;
	}
	
	public int getPc() {
		return m_nextInstruction.getPc();
	}
	
	public XtaInstruction getInstruction() {
		return m_nextInstruction;
	}
	
	public boolean isBranch() {
		return m_isBranch;
	}

}
