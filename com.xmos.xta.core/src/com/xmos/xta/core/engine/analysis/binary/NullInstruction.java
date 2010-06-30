package com.xmos.xta.core.engine.analysis.binary;

public class NullInstruction extends XtaInstruction {
    public NullInstruction(String nmenonic, int opcode, long pc, int numBytes, boolean isFNop) {
        super(nmenonic, opcode, pc, numBytes, false, false, 1, false, false);
        if (isFNop) {
            setIsFnop(true);
        } else {
        	setIsIllegal(true);
        }
    }

    public long getOperandField(String opName) {
    	return 0;
    }

    public String getOperandString() {
    	return "";
    }
}

