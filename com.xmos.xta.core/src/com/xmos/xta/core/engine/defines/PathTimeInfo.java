package com.xmos.xta.core.engine.defines;

import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.utils.NodeCorePair;
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

    public NodeCorePair getNodeCore() {
    	assert(m_from.getNodeCore().equals(m_to.getNodeCore()));
    	return m_from.getNodeCore();
    }

    public boolean equals(Object o) {
		if (o != null){
			if (o instanceof PathTimeInfo){
				PathTimeInfo pti = (PathTimeInfo)o;
				if ((pti.m_from.equals(m_from) && pti.m_to.equals(m_to)) &&				
				    (pti.m_from.getNodeNumber() == m_from.getNodeNumber()) &&
				    (pti.m_from.getCoreNumber() == m_from.getCoreNumber()) &&
				    (pti.m_to.getNodeNumber() == m_to.getNodeNumber()) &&
				    (pti.m_to.getCoreNumber() == m_to.getCoreNumber())) {					
					return true;
				}
			}
		}
		return false;
	}
}
