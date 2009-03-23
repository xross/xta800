package com.xmos.xta.engine.references;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.maps.CodePointInfo;

public class SrcLabelReference extends Reference {
	SrcLabelReference(String ref) {
		super(ref);
	}

	public boolean resolve(Engine engine, int nodeId, int coreId) {
		String ref = getRef();
		if (engine.getSrcLabels().containsKey(ref)) {			
			CodePointInfo info = engine.getEndpoints().get(ref);
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
