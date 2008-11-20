package com.xmos.xta.engine;

public class PathAttributes extends Attributes { 
    private boolean m_isAbsolute;    

    PathAttributes() {
        super();
        m_isAbsolute = false;    
    }

    public void setNumIterations(int numIterations, boolean isAbsolute) {
        setNumIterations(numIterations);
        m_isAbsolute = isAbsolute;
    }

    public boolean isAbsolute() {
        return m_isAbsolute;
    }
    
}
