package com.xmos.xta.engine.instructions;

public class NullInstruction extends XtaInstruction {    
    public NullInstruction(String nmenonic, int opcode, long pc, int numBytes) {
        super(nmenonic, opcode, pc, numBytes, false, false, 1, false, false);
        setIsFnop(true);
    }
    
    public long getOperandField(String opName) {
    	return 0;
    }
    
    public String getOperandString() {
    	return "";
    }
}

