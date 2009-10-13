package com.xmos.xta.core.engine.references.types;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.sections.CodePointInfo;

public class SrcLabelReference extends ReferenceType {
	SrcLabelReference(String ref) {
		super(ref, Type.SRCLABEL);
	}

	public ReferenceType create(Engine engine, String reference) {
		if (checkTypeString(reference, Type.SRCLABEL, 1) &&
		    engine.getSrcLabels().containsKey(getLabel(reference))) {
			return new SrcLabelReference(reference);
		}
		return null;
	}

	public boolean resolve(Engine engine) {
		String ref = getRef();
		String label = getLabel(ref);
		
		if (engine.getSrcLabels().containsKey(label)) {			
			CodePointInfo info = engine.getSrcLabels().get(label);
			setPc(info.getPc());
			return true;
		}
		return false;
	}
	
	public String name() {
		return "SrcLabel";
	}
	
	public String description() {
		return "Source label specified using the source level pragma mechanism";
	}		
}
