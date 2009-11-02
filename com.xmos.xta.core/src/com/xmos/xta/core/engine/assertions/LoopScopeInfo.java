package com.xmos.xta.core.engine.assertions;

import com.xmos.xta.core.engine.references.Reference;

public class LoopScopeInfo {
	private Reference m_reference;
	private boolean m_absolute;

	public LoopScopeInfo(Reference reference, boolean absolute) {
		m_reference = reference;
		m_absolute = absolute;
	}

	public Reference getReference() {
		return m_reference;
	}

	public boolean getAbsolute() {
		return m_absolute;
	}

	public String toString() {
		return String.format("loopscope: %s: %s", m_reference.toString(), m_absolute ? "true" : "false");
	}
}
