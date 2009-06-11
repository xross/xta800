package com.xmos.xta.engine.instructions;

import com.xmos.xta.engine.Config;

public class NullInstruction extends XtaInstruction {    
    public NullInstruction(String nmenonic, Config config, int opcode, int pc, int numBytes) {
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

