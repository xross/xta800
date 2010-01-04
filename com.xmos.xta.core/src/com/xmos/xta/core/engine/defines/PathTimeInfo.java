package com.xmos.xta.core.engine.defines;

import com.xmos.xta.core.engine.TimeUnit;
import com.xmos.xta.core.engine.references.Reference;

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
		return String.format("%s to %s: %s", m_from.toString(), m_to.toString(), m_timeUnit.getDescription(false));
	}
	
    public String getDescription() {
    	return m_from + " to " + m_to;
    }

    public String getValue() {
    	return m_timeUnit.getDescription(true);
    }
    
	public boolean equals(Object o) {
		if (((PathTimeInfo)o).m_from.equals(m_from) && ((PathTimeInfo)o).m_to.equals(m_to)) {
			return true;
		}
		return false;
	}					
}
