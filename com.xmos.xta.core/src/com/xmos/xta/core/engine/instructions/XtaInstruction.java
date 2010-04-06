package com.xmos.xta.core.engine.instructions;

import com.xmos.libs.isa.Instruction;

public abstract class XtaInstruction extends Instruction {
	private String m_functionName;
    private int m_functionOffset;
    private long m_numCycles;
    private boolean m_isLink;
    private boolean m_isBranch;

	XtaInstruction(String nmenonic, int opcode, long pc, int numBytes,
			boolean isLoadStore, boolean canPause, int numCycles, boolean isBranch, boolean isLink) {
		super(nmenonic, opcode, pc, numBytes, isLoadStore, canPause);
		m_functionName = null;
		m_functionOffset = 0;
		m_numCycles = numCycles;
		m_isLink = isLink;
		m_isBranch= isBranch;
		adjustSetcPause(nmenonic);
	}

	public abstract long getOperandField(String opName);

	private void adjustSetcPause(String nmenonic){
		if(getClass() == xs1bXtaInstruction_setc_ru6.class
				|| getClass() == xs1bXtaInstruction_setc_lru6.class) {
			if(getOperandField("op2") == 0x0007){
				this.setCanPause(true);
			} else {
				this.setCanPause(false);
			}
		} else if( getClass() == xs1bXtaInstruction_setc_l2r.class){
			this.setCanPause(true);
		}
	}

	public boolean isBranch(){
		return m_isBranch;
	}

	public boolean isLink(){
		return m_isLink;
	}

	public String toString() {
		return "(" + m_functionName + "+" + m_functionOffset + ")" +
			" 0x" + Long.toHexString(getPc()) + " " + getMnemonic() + " " + getOperandString();
	}

	public void setFunctionName(String functionName) {
		m_functionName = functionName;
	}

	public String getFunctionName() {
		return m_functionName;
	}

	public void setFunctionOffset(int functionOffset) {
		m_functionOffset = functionOffset;
	}

	public int getFunctionOffset() {
		return m_functionOffset;
	}

	public long getNumCycles() {
		return m_numCycles;
	}
}



