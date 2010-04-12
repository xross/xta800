package com.xmos.xta.core.engine.defines;

import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.utils.CoreId;
import com.xmos.xta.core.engine.utils.TimeUnit;

public class PathTimeInfo implements DefineInfo {
	private Reference m_from;
	private Reference m_to;
	private TimeUnit m_timeUnit;

	public PathTimeInfo(Reference from, Reference to, TimeUnit timeUnit) {
		m_from = from;
		m_to = to;
		m_timeUnit = timeUnit;
	}

	public Reference getFrom() {
		return m_from;
	}

	public Reference getTo() {
		return m_to;
	}

	public TimeUnit getTimeUnit() {
		return m_timeUnit;
	}

	public String toString() {
		return String.format("%s to %s: %s", m_from, m_to, m_timeUnit.getDescription(false));
	}

    public String getDescription() {
    	return m_from.getRef() + " " + m_to.getRef();
    }

    public String getValue() {
    	return m_timeUnit.getDescription(true);
    }

    public CoreId getCoreId() {
    	return m_from.getCoreId();
    }

    public Reference[] getReferences() {
    	return new Reference[] {m_from, m_to};
    }
    
    public boolean equals(Object o) {
		if (o != null){
			if (o instanceof PathTimeInfo){
				PathTimeInfo pti = (PathTimeInfo)o;
				if ((pti.m_from.equals(m_from) && pti.m_to.equals(m_to)) &&				
				    (pti.m_from.getCoreId().equals(m_from.getCoreId())) &&
				    (pti.m_to.getCoreId().equals(m_to.getCoreId()))) {					
					return true;
				}
			}
		}
		return false;
	}
}
