package com.xmos.xta.core.engine.references.types;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.sections.CodePointInfo;

public class SrcLabelReference extends ReferenceType {
	SrcLabelReference(String ref) {
		super(ref, Type.SRCLABEL);
	}

	public ReferenceType create(Engine engine, String reference) {
		if (checkTypeString(reference, Type.SRCLABEL, 1) &&
		    isValid(engine.getSrcLabels(), getLabel(reference))) {
			return new SrcLabelReference(reference);
		}
		return null;
	}

	public boolean resolve(Engine engine) {
		String ref = getRef();
		String label = getLabel(ref);
		
		boolean found = false;
		for (CodePointInfo info : engine.getSrcLabels()) {
			if (info.getLabel().equals(label)) {			
				addPc(info.getPc());
				found = true;
			}
		}
		return found;
	}
	
	public String name() {
		return "SrcLabel";
	}
	
	public String description() {
		return "Source label specified using the source level pragma mechanism";
	}		
	
	public boolean isPortable() {
		return true;
	}	
}
