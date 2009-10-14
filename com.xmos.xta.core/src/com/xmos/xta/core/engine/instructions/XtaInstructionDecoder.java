package com.xmos.xta.core.engine.instructions;

import com.xmos.libs.xeloader.XeLoader;

public abstract class XtaInstructionDecoder {
    public abstract XtaInstruction decode(int opcode, long pc);
    
    private static XtaInstructionDecoder m_revB = null;
    private static XtaInstructionDecoder m_revC = null;
    
    public static XtaInstructionDecoder createInstructionDecoder(
    		XeLoader.Architecture architecture) {
    	
		XtaInstructionDecoder instructionDecoder = null;
		switch (architecture) {
		case REVB:
			if (m_revB == null) {
				instructionDecoder = new xs1bXtaInstructionDecoder();
				m_revB = instructionDecoder;
			} else {
				instructionDecoder = m_revB;
			}
			break;
		case REVC:
			if (m_revC == null) {
				instructionDecoder = new xs1bXtaInstructionDecoder();
				m_revC = instructionDecoder;
			} else {
				instructionDecoder = m_revC;
			}
			break;			
		}
		return instructionDecoder;
    }
}
