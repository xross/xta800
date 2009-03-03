package com.xmos.xta.engine.constraint;

public class ConstraintNull extends Constraint {

	public ConstraintNull() {
	}
	
	public boolean isTrue() {
		return true;
	}

	public boolean isFalse(int pc) {
		return false;
	}

	public String getString() {
		String str = "No constraints set";
		return str;
	}

	public Constraint clone() {
		return new ConstraintNull();
	}
	
	
}
