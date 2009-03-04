package com.xmos.xta.engine.constraint;

public class ConstraintNot extends Constraint {
	Constraint m_child;

	public ConstraintNot(Constraint child) {
		assert(child != null);
		m_child = child;
	}
	
	public ConstraintNot(ConstraintNot copy) {
		super(copy);
		m_child = copy.m_child.clone();
	}
	
	public boolean isTrue() {
		if (m_isDone)
			return m_isTrue;

		return !m_child.isTrue();
	}

	public boolean isFalse(int pc) {
		if (m_isDone)
			return !m_isTrue;
		
		m_child.isFalse(pc);
		if (m_child.isDone()) {
			m_isDone = true;
			m_isTrue = m_child.isTrue();
			return m_isTrue;
		}
		return false;
	}

	public String getString() {
		String str = "!" + m_child.getString();
		return str;
	}

	public Constraint clone() {
		return new ConstraintNot(this);
	}
}
