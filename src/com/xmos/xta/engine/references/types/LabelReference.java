package com.xmos.xta.engine.references.types;

import com.xmos.xta.engine.Engine;

public class LabelReference extends ReferenceType {
	LabelReference(String ref) {
		super(ref);
	}
		
	public boolean resolve(Engine engine, int nodeId, int coreId) {
		String ref = getRef();
		
	    if (engine.getLabels().containsKey(ref)) {
		    setPc(engine.getLabels().get(ref));
	        setResolved(true);
	        return true;
	    }
	    return false;		
	}	
	
	public String name() {
		return "Label";
	}
	
	public String description() {
		return "Any source or assembly level symbol defined with respect to an executable section";
	}		
}
