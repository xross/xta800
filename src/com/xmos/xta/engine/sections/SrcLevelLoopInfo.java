package com.xmos.xta.engine.sections;

public class SrcLevelLoopInfo {
    private int m_pc;
    private int m_numIterations;
    
    public SrcLevelLoopInfo(int pc, int numIterations) {
    	m_pc = pc;
    	m_numIterations = numIterations;
    }
 
    public int getPc() {
    	return m_pc;
    }
    
    public int getNumIterations() {
    	return m_numIterations;
    }
}
