package com.xmos.xta.core.engine.references.types;

import com.xmos.libs.util.CoreId;
import com.xmos.libs.util.IteratableSet;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.sections.CodePointInfo;
import com.xmos.xta.core.engine.sections.CodePointTableInfo;

public class EndpointReference extends ReferenceType {
	EndpointReference(String ref, CoreId coreId) {
		super(ref, Type.ENDPOINT, coreId);
	}

	public ReferenceType create(Engine engine, String reference, CoreId coreId) {
		if (checkTypeString(reference, Type.ENDPOINT, 1) &&
		    isValid(engine.getEndpoints(coreId), getLabel(reference))) {
			return new EndpointReference(reference, coreId);
		}
		return null;
	}

	public boolean resolve(Engine engine) {
		String ref = getRef();
		String label = getLabel(ref);

		boolean found = false;
		for (CodePointInfo info : engine.getEndpoints(getCoreId())) {
	    	if (labelsMatch(info.getLabel(), label)) {
				long pc = info.getPc();
				addPc(pc, info.getLabel());

				IteratableSet<CodePointTableInfo> endpointTable = engine.getEndpointTable(getCoreId());

				for(CodePointTableInfo cpti : endpointTable){
					if(cpti.getPc() == info.getPc()){
						setFileName(cpti.getFileName());
		        	    setLineNum(cpti.getLineNumber());
						found = true;
						break;
					}
				}
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
