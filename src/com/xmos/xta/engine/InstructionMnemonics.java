package com.xmos.xta.engine;

import com.xmos.isa.Instruction;
import com.xmos.xeloader.XeLoader.Architecture;

public class InstructionMnemonics {
	private Architecture m_arch;
	
	public InstructionMnemonics(Architecture arch) {
		m_arch = arch;
	}

	public boolean isSsync(Instruction inst) {
		switch (m_arch) {
		case REVA: return inst.getClass() == RevaXtaInstruction_ssync_0r.class;
		case REVB: return inst.getClass() == RevbXtaInstruction_ssync_0r.class;
		}
		return false;
	}
	
	public boolean isMsync(Instruction inst) {
		switch (m_arch) {
		case REVA: return inst.getClass() == RevaXtaInstruction_msync_1r.class;
		case REVB: return inst.getClass() == RevbXtaInstruction_msync_1r.class;
		}
		return false;
	}
	
	public boolean isMjoin(Instruction inst) {
		switch (m_arch) {
		case REVA: return inst.getClass() == RevaXtaInstruction_mjoin_1r.class;
		case REVB: return inst.getClass() == RevbXtaInstruction_mjoin_1r.class;
		}
		return false;
	}
	
	public boolean isSetSr(Instruction inst) {
		switch (m_arch) {
		case REVA: return (inst.getClass() == RevaXtaInstruction_setsr_u6.class) ||
		                  (inst.getClass() == RevaXtaInstruction_setsr_lu6.class); 
		case REVB: return (inst.getClass() == RevbXtaInstruction_setsr_u6.class) ||
                          (inst.getClass() == RevbXtaInstruction_setsr_lu6.class); 
		}
		return false;
	}	
}
