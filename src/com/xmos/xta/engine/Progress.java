package com.xmos.xta.engine;

public class Progress {
	public static enum State {
		PROCESSING_PATHS,
		IDLE,
	}
	
	private State m_state;
    private int m_maxValue;
    private int m_curValue;
	
    static public String getDescriptionName(State state){
    	return getDescription(state);
    }    
    
    Progress(State state, int maxValue, int curValue) {
    	m_state = state;
    	m_maxValue = maxValue;
    	m_curValue = curValue;
	}	

	public void setState(State state) {
		m_state = state;
	}

	public State getState() {
    	return m_state;
    }
    
    public int getMaxValue() {
    	return m_maxValue;
    }

    public int getCurValue() {
    	return m_curValue;
    }
        
    public String getDescription() {
    	return getDescription(m_state);
    }
    
    void resetState(State state, int maxValue) {
    	m_state = state;
    	m_maxValue = maxValue;
    	m_curValue = 0;
    }

    void setCurValue(int curValue) {
        assert(curValue <= m_maxValue);
    	m_curValue = curValue;
    }
    
    void incr() {    	
    	//assert(m_curValue < m_maxValue);
    	++m_curValue;
    }
    
    static private String getDescription(State state) {
    	switch (state) {
        case PROCESSING_PATHS:
        	return "Processing paths";
        	
        case IDLE:
        	return "Idle";

        default:
        	assert false;
            return "";
    	}
    }
}
