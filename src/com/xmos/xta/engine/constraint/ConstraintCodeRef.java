package com.xmos.xta.engine.constraint;

import com.xmos.xta.engine.references.CodeReference;

public class ConstraintCodeRef extends Constraint {
	CodeReference m_codeRef;
	boolean m_seenPc;

	ConstraintCodeRef(CodeReference codeRef) {
		assert(codeRef != null);
		m_codeRef = codeRef;
		m_seenPc = false;
	}

	public boolean isDone() {
		return m_seenPc;
	}

	public boolean isTrue() {
		return m_seenPc;
	}

	public void touchPc(int pc) {
		if (pc == m_codeRef.getPc())
			m_seenPc = true;		
	}

	public String getString() {
		return m_codeRef.getRef();
	}
}
