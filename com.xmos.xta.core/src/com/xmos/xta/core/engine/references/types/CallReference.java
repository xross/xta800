package com.xmos.xta.core.engine.references.types;

import com.xmos.libs.util.CoreId;
import com.xmos.libs.util.IteratableSet;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.sections.CodePointInfo;
import com.xmos.xta.core.engine.sections.CodePointTableInfo;

public class CallReference extends ReferenceType {
	CallReference(String ref, CoreId coreId) {
		super(ref, Type.CALL, coreId);
	}

	public ReferenceType create(Engine engine, String reference, CoreId coreId) {
		if (checkTypeString(reference, Type.CALL, 1) &&
			isValid(engine.getCalls(coreId), getLabel(reference))) {
			return new CallReference(reference, coreId);
		}
		return null;
	}

	public boolean resolve(Engine engine) {
		String ref = getRef();
		String label = getLabel(ref);

		boolean found = false;
		for (CodePointInfo info : engine.getCalls(getCoreId())) {
			if (labelsMatch(info.getLabel(), label)) {
				addPc(info.getPc(), info.getLabel());

				IteratableSet<CodePointTableInfo> callTable = engine.getCallTable(getCoreId());
				
				for(CodePointTableInfo cpti : callTable){
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
		return "Call";
	}

	public String description() {
		return "Call specified using the source level pragma mechanism";
	}

	public boolean isPortable() {
		return true;
	}
}
