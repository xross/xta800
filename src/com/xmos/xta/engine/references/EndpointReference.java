package com.xmos.xta.engine.references;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.maps.SrcLabelInfo;
import com.xmos.xta.engine.maps.SrcTableInfo;

public class EndpointReference extends Reference {
	EndpointReference(String ref) {
		super(ref);
	}

	public boolean resolve(Engine engine, int nodeId, int coreId) {
		String ref = getRef();
		if (engine.getEndpoints().containsKey(ref)) {			
			SrcLabelInfo info = engine.getEndpoints().get(ref);
			setPc(info.getPc());
			
			// Look up the endpoint in the endpoint table
			SrcTableInfo endpoint = engine.getEndpoint(info.getPc());
			setFileName(endpoint.getFileName());
        	setLineNum(endpoint.getLineNumber());

			setResolved(true);
			return true;
		}
		return false;
	}

	public String name() {
		return "Endpoint";
	}
	
	public String description() {
		return "Endpoint specified using the source level pragma mechanism";
	}	
}
