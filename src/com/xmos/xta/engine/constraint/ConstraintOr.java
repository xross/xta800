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

	public boolean isTrue() {
		if (m_isDone)
			return m_isTrue;
		else
			return (m_left.isTrue() || m_right.isTrue());
	}

	public boolean isFalse(int pc) {
		boolean leftFalse = m_left.isFalse(pc);
		boolean rightFalse = m_right.isFalse(pc);
		if (leftFalse && rightFalse) {
			m_isDone = true;
			m_isTrue = false;
			return true;
		}
		return false;
	}

	public String getString() {
		String str = "(" + m_left.getString() + " || " + m_right.getString() + ")";
		return str;
	}
}
