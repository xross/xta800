package com.xmos.xta.core.engine.defines;

import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.utils.CoreId;

public class LoopScopeInfo implements DefineInfo {
	private Reference m_reference;
	private boolean m_absolute;

	public LoopScopeInfo(Reference reference, boolean absolute) {
		m_reference = reference;
		m_absolute = absolute;
	}

	public Reference getReference() {
		return m_reference;
	}

	public boolean getAbsolute() {
		return m_absolute;
	}

	public String toString() {
		return String.format("%s: %s", m_reference, m_absolute ? "true" : "false");
	}

    public String getDescription() {
    	return m_reference.getRef();
    }

    public String getValue() {
    	return m_absolute ? "true" : "false";
    }
    
    public CoreId getCoreId() {
    	return m_reference.getCoreId();
    }

    public Reference[] getReferences() {
    	return new Reference[] {m_reference};
    }
    
    public boolean equals(Object o) {
		boolean ret = false;
		if(o != null) {
			if(o instanceof LoopScopeInfo){
				ret = ((LoopScopeInfo)o).m_reference.equals(m_reference) &&
	                  (((LoopScopeInfo)o).m_reference.getCoreId().equals(m_reference.getCoreId()));
			}
		}
		return ret;
	}
}
