package com.xmos.xta.engine.constraint;

public abstract class Constraint {
	
	protected boolean m_isDone;
	protected boolean m_isTrue;
	
	public Constraint() {
		m_isDone = false;
		m_isTrue = false;
	}
	
	public Constraint(Constraint copy) {
		m_isDone = copy.m_isDone;
		m_isTrue = copy.m_isTrue;
	}
	
	
	protected boolean isDone() {
		return m_isDone;
	}

	public abstract boolean isTrue();
	public abstract boolean isFalse(int pc);
	public abstract String getString();
	public abstract Constraint clone();
}
