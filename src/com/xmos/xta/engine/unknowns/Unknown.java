package com.xmos.xta.engine.unknowns;

public abstract class Unknown implements Comparable<Unknown> {		
	public Unknown() {
	}
	
	public boolean equals(Object obj) {
	    return getDescription().equals(((Unknown)obj).getDescription());
	}

	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + getPc();
		hash = hash * 31 + (getDescription() == null ? 0 : getDescription().hashCode());
	    return hash;
	}

	public String getType(){
		return getClass().getName();
	}
	
	public abstract int getPc();
	public abstract int getBasicBlockId();
	public abstract String getDescription();
	public abstract boolean isUnknown();
}
