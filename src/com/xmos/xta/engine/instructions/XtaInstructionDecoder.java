package com.xmos.xta.engine.instructions;

import com.xmos.xeloader.XeLoader;
import com.xmos.xta.engine.Config;

public abstract class XtaInstructionDecoder {
    public abstract XtaInstruction decode(int opcode, int pc, Config config);
    
    public static XtaInstructionDecoder createInstructionDecoder(
    		XeLoader.Architecture architecture) {
		XtaInstructionDecoder instructionDecoder = null;
		switch (architecture) {
		case REVA:
			instructionDecoder = new RevaXtaInstructionDecoder();
			break;
		case REVB:
			instructionDecoder = new RevbXtaInstructionDecoder();
			break;
		case REVC:
			instructionDecoder = new RevbXtaInstructionDecoder();
			break;
		default:
			assert(false);
		    break;
		}
		return instructionDecoder;
    }
}
