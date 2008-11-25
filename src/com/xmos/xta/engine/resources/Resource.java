package com.xmos.xta.engine.resources;

public interface Resource {

	int getResourceID();
	boolean isInUse();
	void setInUse(boolean b);
	void setMaster(int thread);
	
}
