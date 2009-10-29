package com.xmos.xta.core.engine.assertions;

import com.xmos.xta.core.engine.TimeUnit;

public class FunctionInfo {
	private String m_function;
	private TimeUnit m_timeUnit;

	FunctionInfo(String function, TimeUnit timeUnit) {
		m_function = function;
		m_timeUnit = timeUnit;
	}
	
	public String getFunction() {
		return m_function;
	}

	public TimeUnit getTimeUnit() {
		return m_timeUnit;
	}
	
	public String toString() {
		return String.format("function: %s", m_function);
	}	
}
