package com.xmos.xta.engine;

public class PathAttributes { 
    private boolean m_iterationsSet;    
    private int m_numIterations;    
    private boolean m_isAbsolute;    

    PathAttributes() {
        m_iterationsSet = false;    
        m_numIterations = 0;    
        m_isAbsolute = false;    
    }

    public boolean areIterationsSet() {
        return m_iterationsSet;
    }

    public void setNumIterations(int numIterations, boolean isAbsolute) {
        m_iterationsSet = true;
        m_numIterations = numIterations;
        m_isAbsolute = isAbsolute;
    }

    public boolean isAbsolute() {
        return m_isAbsolute;
    }

    public int getNumIterations() {
        return m_numIterations;
    }
}
