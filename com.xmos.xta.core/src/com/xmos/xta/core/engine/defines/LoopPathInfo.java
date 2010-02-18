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
		return String.format("%s: %d", m_reference.toString(), m_iterations);
	}

    public String getDescription() {
    	return m_reference.toString();
    }

    public String getValue() {
    	return String.format("%d", m_iterations);
    }

	public boolean equals(Object o) {
		return ((LoopPathInfo)o).m_reference.equals(m_reference);
	}
}
