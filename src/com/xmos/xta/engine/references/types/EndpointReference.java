package com.xmos.xta.engine.references.types;

import java.util.Map;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.sections.CodePointInfo;
import com.xmos.xta.engine.sections.CodePointTableInfo;

public class EndpointReference extends ReferenceType {
	EndpointReference(String ref) {
		super(ref, Type.ENDPOINT);
	}

	public ReferenceType create(Engine engine, String reference) {
		if (engine.getEndpoints().containsKey(reference)) {
			return new EndpointReference(reference);
		}
		return null;
	}
	
	public boolean resolve(Engine engine, int nodeId, int coreId) {
		String ref = getRef();
		if (engine.getEndpoints().containsKey(ref)) {			
			CodePointInfo info = engine.getEndpoints().get(ref);
			setPc(info.getPc());
			
			Map<Integer, CodePointTableInfo> endpointTable = engine.getEndpointTable();
			if (!endpointTable.containsKey(info.getPc()))
				return false;
				
		    CodePointTableInfo endpoint = endpointTable.get(info.getPc());			
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
