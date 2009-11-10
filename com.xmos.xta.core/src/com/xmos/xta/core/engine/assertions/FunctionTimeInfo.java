package com.xmos.xta.core.engine.assertions;

import com.xmos.xta.core.engine.TimeUnit;

public class FunctionTimeInfo implements AssertionInfo {
	private String m_function;
	private TimeUnit m_timeUnit;

	public FunctionTimeInfo(String function, TimeUnit timeUnit) {
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
		return String.format("%s: %s", m_function, m_timeUnit.getDescription(false));
	}	
	
    public String getDescription() {
    	return m_function;
    }

    public String getValue() {
    	return m_timeUnit.getDescription(true);
    }

	public boolean equals(Object o) {
		return m_function.equals(((FunctionTimeInfo)o).m_function);
	}
}
