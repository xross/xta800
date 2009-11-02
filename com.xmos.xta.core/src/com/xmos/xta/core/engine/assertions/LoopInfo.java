package com.xmos.xta.core.engine.assertions;

import com.xmos.xta.core.engine.references.Reference;

public class LoopInfo {
	private Reference m_reference;
	private long m_iterations;

	public LoopInfo(Reference reference, long iterations) {
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
		return String.format("loop: %s: %d", m_reference.toString(), m_iterations);
	}
}
