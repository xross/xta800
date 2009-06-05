package com.xmos.xta.engine.references.types;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.sections.CodePointInfo;

public class SrcLabelReference extends ReferenceType {
	SrcLabelReference(String ref) {
		super(ref, Type.SRCLABEL);
	}

	public boolean resolve(Engine engine, int nodeId, int coreId) {
		String ref = getRef();
		if (engine.getSrcLabels().containsKey(ref)) {			
			CodePointInfo info = engine.getSrcLabels().get(ref);
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
