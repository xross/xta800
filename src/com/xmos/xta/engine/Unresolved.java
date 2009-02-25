package com.xmos.xta.engine;

public class Unresolved {	
	private PathBasicBlock m_block;
	private XtaInstruction m_instruction;
	private String m_reason;
	
	public Unresolved(PathBasicBlock block, XtaInstruction instruction, String reason) {
		super();
		m_block = block;
		m_instruction = instruction;
		m_reason = reason;
	}
	
	public PathBasicBlock getBlock() {
		return m_block;
	}
	
	public String getReason() {
		return m_reason;
	}
	
	public XtaInstruction getInstruction() {
		return m_instruction;
	}

}
