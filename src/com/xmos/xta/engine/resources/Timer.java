package com.xmos.xta.engine.resources;

import com.xmos.xta.engine.Expression;

public class Timer implements Resource {
    private boolean m_inUse; 
    
    private boolean m_eventEnabledKnown;
    private boolean m_eventEnabled;
    
    private Expression m_vector;
    private Expression m_environmentVector;
    
    
    
    private int m_timerNo;
    
    public Timer(int timerNo) {
        m_inUse = false;
        m_timerNo = timerNo;
    }
    public boolean isInUse() {
		return m_inUse;
	}

	public void setInUse(boolean inUse) {
		m_inUse = inUse;
	}
	
    public Timer(Timer timer) {
        m_inUse = timer.m_inUse;
        m_eventEnabledKnown = timer.m_eventEnabledKnown;
        m_eventEnabled= timer.m_eventEnabled;
        if(timer.m_vector!= null) m_vector= new Expression(timer.m_vector);
        if(timer.m_environmentVector!= null)  m_environmentVector= new Expression(timer.m_environmentVector);
    }
	public void setEventEnabeled(boolean b) {
		m_eventEnabled = b;
	}
	
	public boolean isEventEnabeled() {
		return m_eventEnabled;
	}
	
	public Expression getEventVector() {
		return m_vector;
	}
	
	public void setEventVector(Expression vector) {
		m_vector = vector;
	}
	
	public int getResourceID() {
		int id = (Resources.RES_TYPE_TIMER&0xf);
		int regId = 0;
		id = id | (regId << 4);
		id = id | (m_timerNo << 8);
		return id;
	}
	private int m_masterThreadId;
	public void setMaster(int thread) {
		m_masterThreadId = thread;
	}
    



}
