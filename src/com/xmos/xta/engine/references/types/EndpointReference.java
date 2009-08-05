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
		if (checkTypeString(reference, Type.ENDPOINT, 1) &&
		    engine.getEndpoints().containsKey(getLabel(reference))) {
			return new EndpointReference(reference);
		}
		return null;
	}
	
	public boolean resolve(Engine engine, int nodeId, int coreId) {
		String ref = getRef();
		String label = getLabel(ref);
		
		if (engine.getEndpoints().containsKey(label)) {			
			CodePointInfo info = engine.getEndpoints().get(label);
			setPc(info.getPc());
			
			Map<Long, CodePointTableInfo> endpointTable = engine.getEndpointTable();
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
