package com.xmos.xta.engine;

import java.util.HashMap;

class InstructionAttributes {
    HashMap<String, Integer> m_timing;  
    
    InstructionAttributes() {
        m_timing = new HashMap<String, Integer>();
        m_timing.put("DIVS_l3r", 8);
        m_timing.put("DIVU_l3r", 8);
        m_timing.put("REMS_l3r", 8);
        m_timing.put("REMU_l3r", 8);
    }
    
    int getTiming(String nmenonic) {
        Integer threadCycles = m_timing.get(nmenonic);
        if (threadCycles != null)
            return threadCycles;
        
        // Default.
        return 1;
    }
}
