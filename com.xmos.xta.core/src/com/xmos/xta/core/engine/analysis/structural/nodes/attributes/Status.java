package com.xmos.xta.core.engine.analysis.structural.nodes.attributes;

public class Status {
	public enum S {
		COMPLETED, INFINITE, UNRESOLVED, ZERO, NOT_AVALIABLE
	}
	private S m_status;

	public Status() {
		m_status = S.COMPLETED;
	}

	public S getStatus() {
		return m_status;
	}

	public void setStatus(S status) {
		m_status = status;
	}

	public boolean isZero() {
		return m_status == S.ZERO;
	}

	public boolean isComplete() {
		return m_status == S.COMPLETED;
	}

	public boolean isInfinite() {
		return m_status == S.INFINITE;
	}

	public boolean isUnresolved() {
		return m_status == S.UNRESOLVED;
	}

	// Keep the worst-case (UNRESOLVED, INFINITE, COMPLETE)
	public void mergeStatus(S s) {
		if (s == S.COMPLETED &&  m_status == S.ZERO) {
			m_status = S.COMPLETED;

		} else if (m_status == S.UNRESOLVED || s == S.UNRESOLVED) {
			m_status = S.UNRESOLVED;

		} else if (m_status == S.INFINITE || s == S.INFINITE) {
			m_status = S.INFINITE;

		} else if (m_status == S.ZERO || s == S.ZERO) {
			m_status = S.ZERO;

		} else if (m_status == S.NOT_AVALIABLE || s == S.NOT_AVALIABLE) {
			m_status = S.NOT_AVALIABLE;
		}
	}

	public String getString() {
	    switch (m_status) {
	    case INFINITE:
	    	return "Infinity";
	    case UNRESOLVED:
	    	return "Unresolved";
	    case NOT_AVALIABLE:
	    	return "n/a";
	    case COMPLETED:
	    case ZERO:
	    default:
	    	assert(false);
	    	return "";
	    }
	}
}
