package com.xmos.xta.engine;

public class InstructionBuffer {
	private final boolean DEBUG = false;
	
    static final int IBUFFER_NUM_BYTES = 8;
    static final int BYTES_PER_WORD = 4;

    int m_pc;
    int m_numBytes;

    public InstructionBuffer() {
        m_numBytes = 0;
	}

    public InstructionBuffer(InstructionBuffer instructionBuffer) {
        m_numBytes = instructionBuffer.m_numBytes;
        m_pc = instructionBuffer.m_pc;
    }
	
    public void setPc(int pc) {
        m_pc = pc;
    }
    
    public int getPc() {
        return m_pc;
    }
    
    int getNumBytes() {
        return m_numBytes;
    }
    
    boolean canIssue(int numBytes) {
        if (m_numBytes < numBytes)
            return false;
        else
            return true;
    }
    
    int fetch() {
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

    void issue(int numBytes) {
        assert(m_numBytes >= numBytes);
        m_numBytes -= numBytes;
    }

    int branchTo(int targetAddress, boolean isLoadStore) {
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

    private boolean isWordAligned(int address) {
        if ((address & 0x3) == 0)
            return true;
        else
            return false;
    }
    
    private void printState() {
        System.out.printf("ibuffer %d bytes from 0x%08x to 0x%08x\n",
                m_numBytes, m_pc, m_pc + m_numBytes);
    }
}
