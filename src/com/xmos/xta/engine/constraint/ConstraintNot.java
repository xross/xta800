package com.xmos.xta.engine.constraint;

public class ConstraintNot extends Constraint {
	Constraint m_child;

	ConstraintNot(Constraint child) {
		assert(child != null);
		m_child = child;
	}

	public boolean isDone() {
		if (m_isDone) {
			return true;
		} else {
			if (m_child.isDone()) {
				m_isTrue = isTrue();
				m_isDone = true;
				return true;
			}
		}
		return false;
	}

	public boolean isTrue() {
		if (m_isDone)
			return m_isTrue;
		else
			return !m_child.isTrue();
	}

	public void touchPc(int pc) {
		if (!m_isDone) {
			m_child.touchPc(pc);
		}
	}

	public String getString() {
		String str = "!" + m_child.getString();
		return str;
	}
}
