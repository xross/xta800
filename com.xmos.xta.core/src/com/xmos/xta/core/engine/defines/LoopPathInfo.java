package com.xmos.xta.core.engine.defines;

import com.xmos.xta.core.engine.references.Reference;

public class LoopPathInfo implements DefineInfo {
	private Reference m_reference;
	private long m_iterations;

	public LoopPathInfo(Reference reference, long iterations) {
		m_reference = reference;
		m_iterations = iterations;
	}

	public Reference getReference() {
		return m_reference;
	}

	public long getIterations() {
		return m_iterations;
	}

	public String toString() {
		return String.format("%s: %d", m_reference, m_iterations);
	}

	public String getDescription() {
		return m_reference.getRef();
	}

	public String getValue() {
		return String.format("%d", m_iterations);
	}

	public boolean equals(Object o) {
		boolean ret = false;
		if (o != null) {
			if (o instanceof LoopPathInfo) {
				ret = ((LoopPathInfo) o).m_reference.equals(m_reference) &&
    	              (((LoopPathInfo)o).m_reference.getNodeNumber() == m_reference.getNodeNumber()) &&
	                  (((LoopPathInfo)o).m_reference.getCoreNumber() == m_reference.getCoreNumber());
			}
		}
		return ret;
	}
}
