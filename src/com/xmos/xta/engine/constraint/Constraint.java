package com.xmos.xta.engine.constraint;

public abstract class Constraint {
	
	protected boolean m_isDone;
	protected boolean m_isTrue;
	
	Constraint() {
		m_isDone = false;
		m_isTrue = false;
	}
	
	protected boolean isDone() {
		return m_isDone;
	}

	public abstract boolean isTrue();
	public abstract boolean isFalse(int pc);
	public abstract String getString();
}
