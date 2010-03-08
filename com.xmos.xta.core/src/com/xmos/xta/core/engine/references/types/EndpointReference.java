package com.xmos.xta.core.engine.references.types;

import java.util.Map;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.sections.CodePointInfo;
import com.xmos.xta.core.engine.sections.CodePointTableInfo;

public class EndpointReference extends ReferenceType {
	EndpointReference(String ref, int nodeNumber, int coreNumber) {
		super(ref, Type.ENDPOINT, nodeNumber, coreNumber);
	}

	public ReferenceType create(Engine engine, String reference, int nodeNumber, int coreNumber) {
		if (checkTypeString(reference, Type.ENDPOINT, 1) &&
		    isValid(engine.getEndpoints(nodeNumber, coreNumber), getLabel(reference))) {
			return new EndpointReference(reference, nodeNumber, coreNumber);
		}
		return null;
	}

	public boolean resolve(Engine engine) {
		String ref = getRef();
		String label = getLabel(ref);

		boolean found = false;
		for (CodePointInfo info : engine.getEndpoints(getNodeNumber(), getCoreNumber())) {
	    	if (labelsMatch(info.getLabel(), label)) {
				long pc = info.getPc();
				addPc(pc, info.getLabel());

				Map<Long, CodePointTableInfo> endpointTable = engine.getEndpointTable(getNodeNumber(), getCoreNumber());
				if (!endpointTable.containsKey(pc))
					return false;

				if (!found) {
				    CodePointTableInfo endpoint = endpointTable.get(pc);
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
