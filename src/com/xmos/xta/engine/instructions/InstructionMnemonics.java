package com.xmos.xta.engine.instructions;

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
		case REVC: return inst.getClass() == RevbXtaInstruction_ssync_0r.class;
		}
		return false;
	}
	
	public boolean isMsync(Instruction inst) {
		switch (m_arch) {
		case REVA: return inst.getClass() == RevaXtaInstruction_msync_1r.class;
		case REVB: return inst.getClass() == RevbXtaInstruction_msync_1r.class;
		case REVC: return inst.getClass() == RevbXtaInstruction_msync_1r.class;
		}
		return false;
	}
	
	public boolean isMjoin(Instruction inst) {
		switch (m_arch) {
		case REVA: return inst.getClass() == RevaXtaInstruction_mjoin_1r.class;
		case REVB: return inst.getClass() == RevbXtaInstruction_mjoin_1r.class;
		case REVC: return inst.getClass() == RevbXtaInstruction_mjoin_1r.class;
		}
		return false;
	}
	
	public boolean isSetSr(Instruction inst) {
		switch (m_arch) {
		case REVA: return (inst.getClass() == RevaXtaInstruction_setsr_u6.class) ||
		                  (inst.getClass() == RevaXtaInstruction_setsr_lu6.class); 
		case REVB: return (inst.getClass() == RevbXtaInstruction_setsr_u6.class) ||
                          (inst.getClass() == RevbXtaInstruction_setsr_lu6.class); 
		case REVC: return (inst.getClass() == RevbXtaInstruction_setsr_u6.class) ||
						  (inst.getClass() == RevbXtaInstruction_setsr_lu6.class); 
		}
		return false;
	}	
	
	public boolean isWait(Instruction inst) {
		switch (m_arch) {
		case REVA: return (inst.getClass() == RevaXtaInstruction_waitef_1r.class) ||
		                  (inst.getClass() == RevaXtaInstruction_waitet_1r.class) ||
		                  (inst.getClass() == RevaXtaInstruction_waiteu_0r.class); 
		case REVB: return (inst.getClass() == RevbXtaInstruction_waitef_1r.class) ||
		                  (inst.getClass() == RevbXtaInstruction_waitet_1r.class) ||
                          (inst.getClass() == RevbXtaInstruction_waiteu_0r.class); 
		case REVC: return (inst.getClass() == RevbXtaInstruction_waitef_1r.class) ||
				          (inst.getClass() == RevbXtaInstruction_waitet_1r.class) ||
				          (inst.getClass() == RevbXtaInstruction_waiteu_0r.class); 
		}		
		return false;
	}
	
	public boolean isConditional(Instruction inst) {
		switch (m_arch) {
		case REVA: return (inst.getClass() == RevaXtaInstruction_brbf_ru6.class) ||
		                  (inst.getClass() == RevaXtaInstruction_brbf_lru6.class) ||
		                  (inst.getClass() == RevaXtaInstruction_brbt_ru6.class) ||
		                  (inst.getClass() == RevaXtaInstruction_brbt_lru6.class) ||
				          (inst.getClass() == RevaXtaInstruction_brff_ru6.class) ||
						  (inst.getClass() == RevaXtaInstruction_brff_lru6.class) ||
						  (inst.getClass() == RevaXtaInstruction_brft_ru6.class) ||
						  (inst.getClass() == RevaXtaInstruction_brft_lru6.class);		
		case REVB: return (inst.getClass() == RevbXtaInstruction_brbf_ru6.class) ||
		                  (inst.getClass() == RevbXtaInstruction_brbf_lru6.class) ||
                          (inst.getClass() == RevbXtaInstruction_brbt_ru6.class) ||
		                  (inst.getClass() == RevbXtaInstruction_brbt_lru6.class) ||
				          (inst.getClass() == RevbXtaInstruction_brff_ru6.class) ||
						  (inst.getClass() == RevbXtaInstruction_brff_lru6.class) ||
						  (inst.getClass() == RevbXtaInstruction_brft_ru6.class) || 
                          (inst.getClass() == RevbXtaInstruction_brft_lru6.class);
		case REVC: return (inst.getClass() == RevbXtaInstruction_brbf_ru6.class) ||
					      (inst.getClass() == RevbXtaInstruction_brbf_lru6.class) ||
					      (inst.getClass() == RevbXtaInstruction_brbt_ru6.class) ||
					      (inst.getClass() == RevbXtaInstruction_brbt_lru6.class) ||
					      (inst.getClass() == RevbXtaInstruction_brff_ru6.class) ||
					      (inst.getClass() == RevbXtaInstruction_brff_lru6.class) ||
						  (inst.getClass() == RevbXtaInstruction_brft_ru6.class) || 
					      (inst.getClass() == RevbXtaInstruction_brft_lru6.class);
		}		
		return false;
	}	
		
	public boolean isBlacpShort(Instruction inst) {
		switch (m_arch) {
		case REVA: return (inst.getClass() == RevaXtaInstruction_blacp_u10.class);
		case REVB: return (inst.getClass() == RevbXtaInstruction_blacp_u10.class);
		case REVC: return (inst.getClass() == RevbXtaInstruction_blacp_u10.class);
		}		
		return false;
	}	

	public boolean isBlacpLong(Instruction inst) {
		switch (m_arch) {
		case REVA: return (inst.getClass() == RevaXtaInstruction_blacp_lu10.class);
		case REVB: return (inst.getClass() == RevbXtaInstruction_blacp_lu10.class);
		case REVC: return (inst.getClass() == RevbXtaInstruction_blacp_lu10.class);
		}		
		return false;
	}

	public boolean isClre(XtaInstruction inst) {
		switch (m_arch) {
		case REVA: return (inst.getClass() == RevaXtaInstruction_clre_0r.class);
		case REVB: return (inst.getClass() == RevbXtaInstruction_clre_0r.class);
		case REVC: return (inst.getClass() == RevbXtaInstruction_clre_0r.class);
		}		
		return false;
	}	
}
