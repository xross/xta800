package com.xmos.xta.engine.resources;

public class Coprocessor implements Resource{
	private int m_procId;
	private boolean m_inUse;
	
    public Coprocessor(int procNo) {
        m_procId=procNo;
    }
    
    public Coprocessor(Coprocessor coprocessor) {
    	m_inUse = coprocessor.m_inUse;
    	m_procId = coprocessor.m_procId;
    }
    
    public boolean isInUse() {
		return m_inUse;
	}

	public void setInUse(boolean inUse) {
		m_inUse = inUse;
	}
    
	public int getResourceID() {
		return 0;
	}

	public void setMaster(int thread) {
		// TODO Auto-generated method stub
		
	}
}
