package com.xmos.xta.core.engine.sections;

public class SrcLevelLoopInfo {
    private long m_pc;
    private int m_numIterations;

    public SrcLevelLoopInfo(long pc, int numIterations) {
    	m_pc = pc;
    	m_numIterations = numIterations;
    }

    public long getPc() {
    	return m_pc;
    }

    public int getNumIterations() {
    	return m_numIterations;
    }
}
