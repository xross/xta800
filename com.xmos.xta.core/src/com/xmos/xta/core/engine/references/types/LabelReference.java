package com.xmos.xta.core.engine.references.types;

import com.xmos.xta.core.engine.Engine;

public class LabelReference extends ReferenceType {
	LabelReference(String ref) {
		super(ref, Type.LABEL);
	}
	
	public ReferenceType create(Engine engine, String reference) {
		if (checkTypeString(reference, Type.LABEL, 1) &&
		    engine.getLabels().containsKey(getLabel(reference))) {
			return new LabelReference(reference);
		}
		return null;
	}
	
	public boolean resolve(Engine engine, int nodeId, int coreId) {
		String ref = getRef();
		String label = getLabel(ref);
		
	    if (engine.getLabels().containsKey(label)) {
		    setPc(engine.getLabels().get(label));
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
