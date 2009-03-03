package com.xmos.xta.engine.references;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.maps.SrcLabelInfo;

public class EndpointReference extends CodeReference {
	EndpointReference(String ref) {
		super(ref);
	}

	public boolean resolve(Engine engine, int nodeId, int coreId) {
		String ref = getRef();
		if (engine.getEndpoints().containsKey(ref)) {			
			SrcLabelInfo info = engine.getEndpoints().get(ref);
			setPc(info.getPc());
			return true;
		}
		return false;
	}
}
