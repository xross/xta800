package com.xmos.xta.engine.constraint;

public class ConstraintOr extends Constraint {
	Constraint m_left;
	Constraint m_right;

	ConstraintOr(Constraint left, Constraint right) {
		assert(left != null);
		assert(right != null);
		m_left = left;
		m_right = right;
	}

	public boolean isDone() {
		if (m_left.isDone() && m_left.isTrue())
			return true;
		else
			return m_right.isDone();
	}

	public boolean isTrue() {
		return false;
	}

	public void touchPc(int pc) {
		if (!isDone()) {
			m_left.touchPc(pc);
			m_right.touchPc(pc);
		}
	}

	public String getString() {
		String str = "(" + m_left.getString() + " || " + m_right.getString() + ")";
		return str;
	}
}
