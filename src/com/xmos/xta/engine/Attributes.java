package com.xmos.xta.engine;

public class Attributes { 
    private boolean m_iterationsSet;    
    private long m_numIterations;  

    Attributes() {
        m_iterationsSet = false;    
        m_numIterations = 0;
    }

    public boolean areIterationsSet() {
        return m_iterationsSet;
    }

    public void setNumIterations(long numIterations) {
        m_iterationsSet = true;
        m_numIterations = numIterations;
    }

    public long getNumIterations() {
        return m_numIterations;
    }
}
