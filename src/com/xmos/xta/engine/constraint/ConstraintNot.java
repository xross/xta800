package com.xmos.xta.engine.constraint;

public class ConstraintNot extends Constraint {
	Constraint m_child;

	ConstraintNot(Constraint child) {
		assert(child != null);
		m_child = child;
	}

	public boolean isTrue() {
		if (m_isDone)
			return m_isTrue;

		return false;
	}

	public boolean isFalse(int pc) {
		if (m_isDone)
			return !m_isTrue;
		
		m_child.isFalse(pc);
		if (m_child.isDone()) {
			m_isDone = true;
			m_isTrue = m_child.isTrue();
			return !m_isTrue;
		}
		return false;
	}

	public String getString() {
		String str = "!" + m_child.getString();
		return str;
	}
}
