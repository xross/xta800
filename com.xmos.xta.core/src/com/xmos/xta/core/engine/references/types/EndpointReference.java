package com.xmos.xta.core.engine.references.types;

import java.util.Map;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.sections.CodePointInfo;
import com.xmos.xta.core.engine.sections.CodePointTableInfo;

public class EndpointReference extends ReferenceType {
	EndpointReference(String ref) {
		super(ref, Type.ENDPOINT);
	}

	public ReferenceType create(Engine engine, String reference) {
		if (checkTypeString(reference, Type.ENDPOINT, 1) &&
		    isValid(engine.getEndpoints(), getLabel(reference))) {
			return new EndpointReference(reference);
		}
		return null;
	}
	
	public boolean resolve(Engine engine) {
		String ref = getRef();
		String label = getLabel(ref);
		
		boolean found = false;
		for (CodePointInfo info : engine.getEndpoints()) {
			if (info.getLabel().equals(label)) {
				addPc(info.getPc());

				Map<Long, CodePointTableInfo> endpointTable = engine.getEndpointTable();
				if (!endpointTable.containsKey(info.getPc()))
					return false;

				if (!found) {
				    CodePointTableInfo endpoint = endpointTable.get(info.getPc());			
					setFileName(endpoint.getFileName());
		        	setLineNum(endpoint.getLineNumber());
				}
				
				found = true;
			}
		}

		if (found) {
			setResolved(true);
		}
		return found;
	}

	public String name() {
		return "Endpoint";
	}
	
	public String description() {
		return "Endpoint specified using the source level pragma mechanism";
	}	
	
	public boolean isPortable() {
		return true;
	}	
}
