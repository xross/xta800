package com.xmos.xta.engine.analysis.controlflow;

public class InstructionBuffer implements Comparable<InstructionBuffer>{
	
    static final int IBUFFER_NUM_BYTES = 8;
    static final int BYTES_PER_WORD = 4;

    long m_pc;
    int m_numBytes;

    public InstructionBuffer(long startPc) {
    	m_pc = startPc;
        m_numBytes = 0;
	}
    
    public InstructionBuffer(InstructionBuffer instructionBuffer) {
        m_numBytes = instructionBuffer.m_numBytes;
        m_pc = instructionBuffer.m_pc;
    }
    
    public void setPc(long pc) {
        m_pc = pc;
    }
    
    public int getNumBytes() {
        return m_numBytes;
    }
    
    public boolean canIssue(int numBytes) {
        if (m_numBytes < numBytes)
            return false;
        else
            return true;
    }
    
    public int fetch() {
        // Need room for a word before fetching
        if (m_numBytes <= (IBUFFER_NUM_BYTES - BYTES_PER_WORD))
        {
            // Aligned fetches can get an entire word
            if (isWordAligned(m_pc + m_numBytes)) {
                m_numBytes += BYTES_PER_WORD;
                return BYTES_PER_WORD;
            } else {
                m_numBytes += BYTES_PER_WORD / 2;
                return BYTES_PER_WORD / 2;
            }
        }
        return 0;
    }

    public void issue(int numBytes) {
        assert(m_numBytes >= numBytes);
        m_numBytes -= numBytes;
    }

    public int branchTo(long targetAddress, boolean isLoadStore) {
        m_pc = targetAddress;
        
        if (isLoadStore) {
            m_numBytes = 0;
        } else {
            // Word-aligned branches will fetch 2 halves
            if (isWordAligned(targetAddress)) {
                m_numBytes = BYTES_PER_WORD;
                
            } else {
                m_numBytes = BYTES_PER_WORD / 2;
            }
        }
        return m_numBytes;
	}

    private boolean isWordAligned(long address) {
        if ((address & 0x3) == 0)
            return true;
        else
            return false;
    }

	public int compareTo(InstructionBuffer o) {
		if(m_pc - o.m_pc != 0){
			return Long.signum(m_pc - o.m_pc);
		} else {
			return Long.signum(m_numBytes - o.m_numBytes);
		}
		
	}

}
