package com.xmos.xta.engine.constraint;

public class ConstraintAnd extends Constraint {
	Constraint m_left;
	Constraint m_right;

	ConstraintAnd(Constraint left, Constraint right) {
		assert(left != null);
		assert(right != null);
		m_left = left;
		m_right = right;
	}

	public boolean isDone() {
		if (m_left.isDone() && m_right.isDone())
			return true;
		else
			return false;
	}

	public boolean isTrue() {
		return (m_left.isTrue() && m_right.isTrue());
	}

	public void touchPc(int pc) {
		if (!isDone()) {
			m_left.touchPc(pc);
			m_right.touchPc(pc);
		}
	}

	public String getString() {
		String str = "(" + m_left.getString() + " && " + m_right.getString() + ")";
		return str;
	}
}
