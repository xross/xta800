package com.xmos.xta.engine;

import java.util.HashMap;

class InstructionAttributes {
    HashMap<String, Integer> m_timing;  
    
    InstructionAttributes() {
        m_timing = new HashMap<String, Integer>();

        m_timing.put(new RevbXtaInstruction_divs_l3r(0, 0).getNmenonic(), 8);
        m_timing.put(new RevbXtaInstruction_divu_l3r(0, 0).getNmenonic(), 8);
        m_timing.put(new RevbXtaInstruction_rems_l3r(0, 0).getNmenonic(), 8);
        m_timing.put(new RevbXtaInstruction_remu_l3r(0, 0).getNmenonic(), 8);
    }
    
    int getTiming(String nmenonic) {
        Integer threadCycles = m_timing.get(nmenonic);
        if (threadCycles != null)
            return threadCycles;
        
        // Default.
        return 1;
    }
}
