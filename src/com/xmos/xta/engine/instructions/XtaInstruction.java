package com.xmos.xta.engine.instructions;

import com.xmos.isa.Instruction;
import com.xmos.xta.engine.Config;

public abstract class XtaInstruction extends Instruction {
	private String m_functionName;
    private int m_functionOffset;	
    private long m_numCycles;
    private int m_basicNumCycles;
    private Config m_config;
    
    private boolean m_isLink;
    private boolean m_isBranch;
	
	private boolean m_isUnknown;
	private int m_basicBlockId;
	
	XtaInstruction(String nmenonic, Config config, int opcode, int pc, int numBytes, 
			boolean isLoadStore, boolean canPause, int numCycles, boolean isBranch, boolean isLink) {
		super(nmenonic, opcode, pc, numBytes, isLoadStore, canPause);
		m_functionName = null;
		m_functionOffset = 0;
		m_numCycles = 0;
		m_basicNumCycles = numCycles;
		m_numCycles = numCycles;
		m_config = config;
		m_isUnknown = canPause;
		m_basicBlockId = 0;
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
	
	public void setNumCycles(long numCycles) {
		// Makes sure the user cannot configure the instruction to take
		// less time than is physically possible.
		if (numCycles > m_basicNumCycles) {
		    m_numCycles = numCycles;
		}
		else {
		    m_numCycles = m_basicNumCycles;
		}
		m_isUnknown = false;
	}
	
	public long getNumCycles() {
		return m_numCycles;
	}

	public void resetNumCycles() {
	    m_numCycles = m_basicNumCycles;
	    m_isUnknown = true;
	}
	
	public void setTimeTaken(double timeTaken) {
    	int threadFactor = (m_config.m_numThreads > 4) ? m_config.m_numThreads : 4;		
       	setNumCycles((int)((timeTaken * m_config.m_frequency) / (threadFactor * 1000)));
	}
	
	public double getTimeTaken() {
    	int threadFactor = (m_config.m_numThreads > 4) ? m_config.m_numThreads : 4;
        return ((double)(threadFactor * 1000) / (double)m_config.m_frequency) * m_numCycles;
	}
	
	public boolean isUnknown() {
		return m_isUnknown;
	}
	
	public void setBasicBlockId(int basicBlockId) {
		m_basicBlockId = basicBlockId;
	}
	
	public int getBasicBlockId() {
		return m_basicBlockId;
	}
	
	void setConfig(Config config) {
		m_config = config;
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

class NullInstruction extends XtaInstruction {    
    NullInstruction(String nmenonic, Config config, int opcode, int pc, int numBytes) {
        super(nmenonic, config, opcode, pc, numBytes, false, false, 1, false, false);
        setIsFnop(true);
    }
    
    public int getOperandField(String opName) {
    	return 0;
    }
    
    public String getOperandString() {
    	return "";
    }

}


