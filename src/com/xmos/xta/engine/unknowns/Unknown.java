package com.xmos.xta.engine.unknowns;

public abstract class Unknown implements Comparable<Unknown> {		

	public Unknown() {
	}
	
	public abstract int getPc();
	public abstract int getBasicBlockId();
	public abstract String getDescription();
	public abstract boolean isUnknown();
}
