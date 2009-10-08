package com.xmos.xta.core.engine.instructions;

import com.xmos.libs.isa.Instruction;
import com.xmos.libs.xeloader.XeLoader.Architecture;

public class InstructionMnemonics {
	private Architecture m_arch;
	
	public InstructionMnemonics(Architecture arch) {
		m_arch = arch;
	}
	/*
	public boolean isMultiCycle(Instruction inst) {
		switch (m_arch) {
		case REVB: return (inst.getClass() == xs1bXtaInstruction_divs_l3r.class) ||
                          (inst.getClass() == xs1bXtaInstruction_divu_l3r.class) ||
                          (inst.getClass() == xs1bXtaInstruction_rems_l3r.class) ||
                          (inst.getClass() == xs1bXtaInstruction_remu_l3r.class); 
		case REVC: return (inst.getClass() == xs1bXtaInstruction_divs_l3r.class) ||
			              (inst.getClass() == xs1bXtaInstruction_divu_l3r.class) ||
			              (inst.getClass() == xs1bXtaInstruction_rems_l3r.class) ||
			              (inst.getClass() == xs1bXtaInstruction_remu_l3r.class); 
		}
		return false;
	}
	*/
	
	public boolean isSsync(Instruction inst) {
		switch (m_arch) {
		case REVB: return inst.getClass() == xs1bXtaInstruction_ssync_0r.class;
		case REVC: return inst.getClass() == xs1bXtaInstruction_ssync_0r.class;
		}
		return false;
	}
	
	public boolean isMsync(Instruction inst) {
		switch (m_arch) {
		case REVB: return inst.getClass() == xs1bXtaInstruction_msync_1r.class;
		case REVC: return inst.getClass() == xs1bXtaInstruction_msync_1r.class;
		}
		return false;
	}
	
	public boolean isMjoin(Instruction inst) {
		switch (m_arch) {
		case REVB: return inst.getClass() == xs1bXtaInstruction_mjoin_1r.class;
		case REVC: return inst.getClass() == xs1bXtaInstruction_mjoin_1r.class;
		}
		return false;
	}
	
	public boolean isSetSr(Instruction inst) {
		switch (m_arch) {
		case REVB: return (inst.getClass() == xs1bXtaInstruction_setsr_u6.class) ||
                          (inst.getClass() == xs1bXtaInstruction_setsr_lu6.class); 
		case REVC: return (inst.getClass() == xs1bXtaInstruction_setsr_u6.class) ||
						  (inst.getClass() == xs1bXtaInstruction_setsr_lu6.class); 
		}
		return false;
	}	
	
	public boolean isWait(Instruction inst) {
		switch (m_arch) {
		case REVB: return (inst.getClass() == xs1bXtaInstruction_waitef_1r.class) ||
		                  (inst.getClass() == xs1bXtaInstruction_waitet_1r.class) ||
                          (inst.getClass() == xs1bXtaInstruction_waiteu_0r.class); 
		case REVC: return (inst.getClass() == xs1bXtaInstruction_waitef_1r.class) ||
				          (inst.getClass() == xs1bXtaInstruction_waitet_1r.class) ||
				          (inst.getClass() == xs1bXtaInstruction_waiteu_0r.class); 
		}		
		return false;
	}
	
	public boolean isConditional(Instruction inst) {
		switch (m_arch) {
		case REVB: return (inst.getClass() == xs1bXtaInstruction_brbf_ru6.class) ||
		                  (inst.getClass() == xs1bXtaInstruction_brbf_lru6.class) ||
                          (inst.getClass() == xs1bXtaInstruction_brbt_ru6.class) ||
		                  (inst.getClass() == xs1bXtaInstruction_brbt_lru6.class) ||
				          (inst.getClass() == xs1bXtaInstruction_brff_ru6.class) ||
						  (inst.getClass() == xs1bXtaInstruction_brff_lru6.class) ||
						  (inst.getClass() == xs1bXtaInstruction_brft_ru6.class) || 
                          (inst.getClass() == xs1bXtaInstruction_brft_lru6.class);
		case REVC: return (inst.getClass() == xs1bXtaInstruction_brbf_ru6.class) ||
					      (inst.getClass() == xs1bXtaInstruction_brbf_lru6.class) ||
					      (inst.getClass() == xs1bXtaInstruction_brbt_ru6.class) ||
					      (inst.getClass() == xs1bXtaInstruction_brbt_lru6.class) ||
					      (inst.getClass() == xs1bXtaInstruction_brff_ru6.class) ||
					      (inst.getClass() == xs1bXtaInstruction_brff_lru6.class) ||
						  (inst.getClass() == xs1bXtaInstruction_brft_ru6.class) || 
					      (inst.getClass() == xs1bXtaInstruction_brft_lru6.class);
		}		
		return false;
	}	
		
	public boolean isBlacpShort(Instruction inst) {
		switch (m_arch) {
		case REVB: return (inst.getClass() == xs1bXtaInstruction_blacp_u10.class);
		case REVC: return (inst.getClass() == xs1bXtaInstruction_blacp_u10.class);
		}		
		return false;
	}	

	public boolean isBlacpLong(Instruction inst) {
		switch (m_arch) {
		case REVB: return (inst.getClass() == xs1bXtaInstruction_blacp_lu10.class);
		case REVC: return (inst.getClass() == xs1bXtaInstruction_blacp_lu10.class);
		}		
		return false;
	}

	public boolean isClre(XtaInstruction inst) {
		switch (m_arch) {
		case REVB: return (inst.getClass() == xs1bXtaInstruction_clre_0r.class);
		case REVC: return (inst.getClass() == xs1bXtaInstruction_clre_0r.class);
		}		
		return false;
	}
	
	public boolean isDivide(XtaInstruction inst) {
		switch (m_arch) {
		case REVB: return (inst.getClass() == xs1bXtaInstruction_divs_l3r.class) ||
                          (inst.getClass() == xs1bXtaInstruction_divu_l3r.class);
        case REVC: return (inst.getClass() == xs1bXtaInstruction_divs_l3r.class) ||
                 	      (inst.getClass() == xs1bXtaInstruction_divu_l3r.class);
		}
		return false;
	}

	public boolean isRemainder(XtaInstruction inst) {
		switch (m_arch) {
		case REVB: return (inst.getClass() == xs1bXtaInstruction_rems_l3r.class) ||
                          (inst.getClass() == xs1bXtaInstruction_remu_l3r.class);
        case REVC: return (inst.getClass() == xs1bXtaInstruction_rems_l3r.class) ||
                 	      (inst.getClass() == xs1bXtaInstruction_remu_l3r.class);
		}
		return false;
	}		
}
