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
		if (m_isDone) {
			return true;
		} else {
			if ((m_left.isDone()  && m_left.isTrue()) ||
			    (m_right.isDone() && m_right.isTrue())) {
				m_isTrue = true;
				m_isDone = true;
				return true;

			} else if (m_left.isDone() && m_right.isDone()) {
				m_isTrue = false;
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
			return (m_left.isTrue() || m_right.isTrue());
	}

	public void touchPc(int pc) {
		if (!m_isDone) {
			m_left.touchPc(pc);
			m_right.touchPc(pc);
		}
	}

	public String getString() {
		String str = "(" + m_left.getString() + " || " + m_right.getString() + ")";
		return str;
	}
}
