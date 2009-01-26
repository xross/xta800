package com.xmos.xta.engine.references;

import com.xmos.xta.engine.Engine;

public class LabelReference extends CodeReference {

	LabelReference(String ref) {
		super(ref);
	}
		
	public boolean resolve(Engine engine, int m_nodeId, int m_coreId) {
		String ref = getRef();
		
	    if (engine.isLabel(ref)) {
		    setPc(engine.getLabelPc(ref));
	        setResolved(true);
	        return true;
	    }
	    return false;		
	}	
}
