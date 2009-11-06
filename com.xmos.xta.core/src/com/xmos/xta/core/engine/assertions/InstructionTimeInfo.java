package com.xmos.xta.core.engine.assertions;

import com.xmos.xta.core.engine.TimeUnit;
import com.xmos.xta.core.engine.references.Reference;

public class InstructionTimeInfo implements AssertionInfo {
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
		return String.format("%s: %s", m_reference.toString(), m_timeUnit.getDescription(false));
	}
	
    public String getDescription() {
    	return m_reference.toString();
    }

    public String getValue() {
    	return m_timeUnit.getDescription(false);
    }	
}
