package com.xmos.xta.engine;

import com.xmos.xeloader.XeLoader;

public abstract class XtaInstructionDecoder {
    public abstract XtaInstruction decode(int opcode, int pc);
    
    static XtaInstructionDecoder createInstrcutionDecoder(
    		XeLoader.Architecture architecture) {
		XtaInstructionDecoder instructionDecoder = null;
		switch (architecture) {
		case REVA:
			instructionDecoder = new RevaXtaInstructionDecoder();
			break;
		case REVB:
			instructionDecoder = new RevbXtaInstructionDecoder();
			break;
		default:
			assert(false);
		    break;
		}
		return instructionDecoder;
    }
}
