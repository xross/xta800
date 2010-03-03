package com.xmos.xta.core.engine.defines;

import com.xmos.xta.core.engine.references.Reference;

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
		return String.format("%s: %s", m_reference.toString(), m_absolute ? "true" : "false");
	}

    public String getDescription() {
    	return m_reference.toString();
    }

    public String getValue() {
    	return m_absolute ? "true" : "false";
    }

	public boolean equals(Object o) {
		boolean ret = false;
		if(o != null) {
			if(o instanceof LoopScopeInfo){
				ret = ((LoopScopeInfo)o).m_reference.equals(m_reference) &&
	                  (((LoopScopeInfo)o).m_reference.getNodeNumber() == m_reference.getNodeNumber()) &&
                      (((LoopScopeInfo)o).m_reference.getCoreNumber() == m_reference.getCoreNumber());
			}
		}
		return ret;
	}
}
