package com.xmos.xta.engine.instructions;

import com.xmos.isa.Instruction;
import com.xmos.xta.engine.Config;

public abstract class XtaInstruction extends Instruction {
	private String m_functionName;
    private int m_functionOffset;	
    private long m_numCycles;
    private Config m_config;    
    private boolean m_isLink;
    private boolean m_isBranch;
	
	XtaInstruction(String nmenonic, Config config, int opcode, int pc, int numBytes, 
			boolean isLoadStore, boolean canPause, int numCycles, boolean isBranch, boolean isLink) {
		super(nmenonic, opcode, pc, numBytes, isLoadStore, canPause);
		m_functionName = null;
		m_functionOffset = 0;
		m_numCycles = numCycles;
		m_config = config;
		m_isLink = isLink;
		m_isBranch= isBranch;
	}
	
	public boolean isBranch(){
		return m_isBranch;
	}
	public boolean isLink(){
		return m_isLink;
	}
	
	public String toString(){
		return "0x" + Integer.toHexString(getPc()) + " " + getNmenonic() + " " + getOperandString();
	}
	
	public abstract int getOperandField(String opName);    		   
	
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
		
	public Config getConfig() {
		return m_config;
	}

	public boolean isNop() {
		if(isFnop()) return true;
		
		
		if(((getOpcode()>>(8*this.getNumBytes()))&0xFFFF) == 0x9000){
			return true;
		}
		return false;
	}
}



