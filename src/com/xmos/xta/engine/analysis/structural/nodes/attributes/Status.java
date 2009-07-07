package com.xmos.xta.engine.analysis.structural.nodes.attributes;

public class Status {
	public enum S {
		COMPLETED, INFINITE, UNRESOLVED
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
	
	public boolean isComplete() {
		return m_status == S.COMPLETED;
	}

	public boolean isInfinite() {
		return m_status == S.INFINITE;
	}

	public boolean isUnresolved() {
		return m_status == S.UNRESOLVED;
	}
}
