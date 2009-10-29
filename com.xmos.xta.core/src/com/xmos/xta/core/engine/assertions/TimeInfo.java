package com.xmos.xta.core.engine.assertions;

import com.xmos.xta.core.engine.TimeUnit;
import com.xmos.xta.core.engine.references.Reference;

public class TimeInfo {
	private Reference m_reference;
	private TimeUnit m_timeUnit;

	public TimeInfo(Reference reference, TimeUnit timeUnit) {
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
		return String.format("time: %s: %s", m_reference.toString(), m_timeUnit.toString());
	}
}
