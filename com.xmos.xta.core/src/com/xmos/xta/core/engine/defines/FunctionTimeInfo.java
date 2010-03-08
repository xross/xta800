package com.xmos.xta.core.engine.defines;

import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.utils.TimeUnit;

public class FunctionTimeInfo implements DefineInfo {
	private Reference m_function;
	private TimeUnit m_timeUnit;

	public FunctionTimeInfo(Reference function, TimeUnit timeUnit) {
		m_function = function;
		m_timeUnit = timeUnit;
	}

	public Reference getFunction() {
		return m_function;
	}

	public TimeUnit getTimeUnit() {
		return m_timeUnit;
	}

	public String toString() {
		return String.format("%s: %s", m_function.getRef(), m_timeUnit.getDescription(false));
	}

    public String getDescription() {
    	return m_function.getRef();
    }

    public String getValue() {
    	return m_timeUnit.getDescription(true);
    }

	public boolean equals(Object o) {
		boolean ret = false;
		if (o != null) {
			if (o instanceof FunctionTimeInfo) {
				ret = ((FunctionTimeInfo)o).m_function.equals(m_function) &&
				      (((FunctionTimeInfo)o).m_function.getNodeNumber() == m_function.getNodeNumber()) &&
				      (((FunctionTimeInfo)o).m_function.getCoreNumber() == m_function.getCoreNumber());
			}
		}
		return ret;
	}
}
