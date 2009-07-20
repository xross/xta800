package com.xmos.xta.engine.instructions;

public class NullInstruction extends XtaInstruction {    
    public NullInstruction(String nmenonic, int opcode, int pc, int numBytes) {
        super(nmenonic, opcode, pc, numBytes, false, false, 1, false, false);
        setIsFnop(true);
    }
    
    public int getOperandField(String opName) {
    	return 0;
    }
    
    public String getOperandString() {
    	return "";
    }
}

