package com.xmos.xta.engine.references;

import com.xmos.xta.engine.Engine;

public abstract class Reference {		
	private String m_ref;
	private String m_shortRef;
	private int m_pc;
	private boolean m_resolved;
    
	Reference(String ref) {
		m_ref = ref;
		m_shortRef = ref;
		m_pc = 0;
		m_resolved = false;
	}
	
	public abstract String name();
	public abstract String description();
	public abstract boolean resolve(Engine engine, int nodeId, int coreId);
	
	public String getRef() {
	    return m_ref;	    
	}
	
	public void setRef(String ref) {
		m_ref = ref;
	}
	
    public String getShortRef() {
        return m_shortRef;	
    }

	public void setShortRef(String shortRef) {
		m_shortRef = shortRef;
	}
    
    public boolean getResolved() {
    	return m_resolved;
    }

    public void setResolved(boolean resolved) {
    	m_resolved = resolved;
    }
    
	public int getPc() {
		return m_pc;
	}	

	public void setPc(int pc) {
	    m_pc = pc;
	}
}
