package com.xmos.xta.engine.constraint;

public abstract class Constraint {
	
	protected boolean m_isDone;
	protected boolean m_isTrue;
	
	Constraint() {
		m_isDone = false;
		m_isTrue = false;
	}
	
	public abstract boolean isDone();
	public abstract boolean isTrue();
	public abstract void touchPc(int pc);
	public abstract String getString();
}
