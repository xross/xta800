package com.xmos.xta.core.engine.references.types;

import java.util.Map;
import java.util.Set;

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

	public boolean resolve(Engine engine) {
		String ref = getRef();
		String label = getLabel(ref);

	    Map<String, Set<Long>> labels = engine.getLabels();
		if (labels.containsKey(label)) {
	    	for (Long pc : labels.get(label)) {
	    		addPc(pc);
	    	}
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

	public boolean isPortable() {
		return true;
	}
}
