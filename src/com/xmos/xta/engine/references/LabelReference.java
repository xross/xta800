package com.xmos.xta.engine.references;

import com.xmos.xta.engine.Engine;

public class LabelReference extends CodeReference {
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
}
