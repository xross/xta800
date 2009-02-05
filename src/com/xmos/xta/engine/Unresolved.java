package com.xmos.xta.engine;

public class Unresolved {	
	private Block m_block;
	private XtaInstruction m_instruction;
	private String m_reason;
	
	public Unresolved(Block block, XtaInstruction instruction, String reason) {
		super();
		m_block = block;
		m_instruction = instruction;
		m_reason = reason;
	}
	
	public Block getBlock() {
		return m_block;
	}
	
	public String getReason() {
		return m_reason;
	}
	
	public XtaInstruction getInstruction() {
		return m_instruction;
	}

}
