package com.xmos.xta.engine.instructions;

import com.xmos.xeloader.XeLoader;

public abstract class XtaInstructionDecoder {
    public abstract XtaInstruction decode(int opcode, long pc);
    
    private static XtaInstructionDecoder m_revB = null;
    private static XtaInstructionDecoder m_revC = null;
    
    public static XtaInstructionDecoder createInstructionDecoder(
    		XeLoader.Architecture architecture) {
		XtaInstructionDecoder instructionDecoder = null;
		switch (architecture) {
		case REVB:
			if(m_revB == null){
				instructionDecoder = new RevbXtaInstructionDecoder();
				m_revB = instructionDecoder;
			} else {
				instructionDecoder = m_revB;
			}
			break;
		case REVC:
			if(m_revC == null){
				instructionDecoder = new RevbXtaInstructionDecoder();
				m_revC = instructionDecoder;
			} else {
				instructionDecoder = m_revC;
			}
			break;
		}
		assert(instructionDecoder != null);
		return instructionDecoder;
    }
}
