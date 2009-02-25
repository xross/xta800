package com.xmos.xta.engine.constraint;

public class ConstraintNot extends Constraint {
	Constraint m_child;

	ConstraintNot(Constraint child) {
		assert(child != null);
		m_child = child;
	}

	public boolean isDone() {
		return m_child.isDone();
	}

	public boolean isTrue() {
		return !m_child.isTrue();
	}

	public void touchPc(int pc) {
		if (!isDone()) {
			m_child.touchPc(pc);
		}
	}

	public String getString() {
		String str = "!" + m_child.getString();
		return str;
	}
}
