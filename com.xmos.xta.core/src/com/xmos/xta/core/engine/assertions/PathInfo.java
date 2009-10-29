package com.xmos.xta.core.engine.assertions;

import com.xmos.xta.core.engine.TimeUnit;

public class PathInfo {
	private String m_from;
	private String m_to;
	private TimeUnit m_timeUnit;

	public PathInfo(String from, String to, TimeUnit timeUnit) {
		m_from = from;
		m_to = to;
		m_timeUnit = timeUnit;
	}
	
	public String getFrom() {
		return m_from;
	}

	public String getTo() {
		return m_to;
	}
	
	public TimeUnit getTimeUnit() {
		return m_timeUnit;
	}

	public String toString() {
		return String.format("path: %s to %s", m_from, m_to);
	}
}
