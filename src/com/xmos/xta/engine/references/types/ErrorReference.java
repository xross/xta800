package com.xmos.xta.engine.references.types;

import com.xmos.xta.engine.Engine;

public class ErrorReference extends ReferenceType {
	ErrorReference(String ref) {
		super(ref, Type.ERROR);
	}
	
	public ReferenceType create(Engine engine, String reference) {
		assert(false);
		return null;
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
