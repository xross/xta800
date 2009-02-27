package com.xmos.xta.engine.constraint;

import com.xmos.xta.engine.references.CodeReference;

public class ConstraintCodeRef extends Constraint {
	CodeReference m_codeRef;

	ConstraintCodeRef(CodeReference codeRef) {
		assert(codeRef != null);
		m_codeRef = codeRef;
	}

	public boolean isDone() {
		return m_isDone;
	}

	public boolean isTrue() {
		return m_isTrue;
	}

	public boolean isFalse(int pc) {
		if (pc == m_codeRef.getPc()) {
			m_isDone = true;
			m_isTrue = true;
		}
		return false;
	}

	public String getString() {
		return m_codeRef.getRef();
	}
}
