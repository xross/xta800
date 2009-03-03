package com.xmos.xta.engine.references;

import com.xmos.xta.engine.Engine;

public class ErrorReference extends Reference {
	ErrorReference(String ref) {
		super(ref);
	}
		
	public boolean resolve(Engine engine, int nodeId, int coreId) {
        return false;
	}	
	
	public String name() {
		return "Error";
	}
	
	public String description() {
		return "Error";
	}		
}
