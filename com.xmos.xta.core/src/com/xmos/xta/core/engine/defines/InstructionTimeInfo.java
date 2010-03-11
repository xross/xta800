package com.xmos.xta.core.engine.defines;

import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.utils.NodeCorePair;
import com.xmos.xta.core.engine.utils.TimeUnit;

public class InstructionTimeInfo implements DefineInfo {
	private Reference m_reference;
	private TimeUnit m_timeUnit;

	public InstructionTimeInfo(Reference reference, TimeUnit timeUnit) {
		m_reference = reference;
		m_timeUnit = timeUnit;
	}

	public Reference getReference() {
		return m_reference;
	}

	public TimeUnit getTimeUnit() {
		return m_timeUnit;
	}

	public String toString() {
		return String.format("%s: %s", m_reference, m_timeUnit.getDescription(false));
	}

    public String getDescription() {
    	return m_reference.getRef();
    }

    public String getValue() {
    	return m_timeUnit.getDescription(true);
    }

    public NodeCorePair getNodeCore() {
    	return m_reference.getNodeCore();
    }

    public Reference[] getReferences() {
    	return new Reference[] {m_reference};
    }
        
	public boolean equals(Object o) {
		boolean ret = false;
		if (o!=null) {
			if (o instanceof InstructionTimeInfo) {
				ret = ((InstructionTimeInfo)o).m_reference.equals(m_reference) &&
			          (((InstructionTimeInfo)o).m_reference.getNodeNumber() == m_reference.getNodeNumber()) &&
			          (((InstructionTimeInfo)o).m_reference.getCoreNumber() == m_reference.getCoreNumber());
			}
		}
		return ret;
	}
}
