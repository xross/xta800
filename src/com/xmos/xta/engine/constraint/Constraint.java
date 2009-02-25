package com.xmos.xta.engine.constraint;

public abstract class Constraint {		
	Constraint() {
	}
	
	public abstract boolean isDone();
	public abstract boolean isTrue();
	public abstract void touchPc(int pc);
	public abstract String getString();
}
