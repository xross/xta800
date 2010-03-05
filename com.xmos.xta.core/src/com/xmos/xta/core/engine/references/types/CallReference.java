package com.xmos.xta.core.engine.references.types;

import java.util.Map;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.sections.CodePointInfo;
import com.xmos.xta.core.engine.sections.CodePointTableInfo;

public class CallReference extends ReferenceType {
	CallReference(String ref, int nodeNumber, int coreNumber) {
		super(ref, Type.CALL, nodeNumber, coreNumber);
	}

	public ReferenceType create(Engine engine, String reference, int nodeNumber, int coreNumber) {
		if (checkTypeString(reference, Type.CALL, 1) &&
			isValid(engine.getCalls(nodeNumber, coreNumber), getLabel(reference))) {
			return new CallReference(reference, nodeNumber, coreNumber);
		}
		return null;
	}

	public boolean resolve(Engine engine) {
		String ref = getRef();
		String label = getLabel(ref);

		boolean found = false;
		for (CodePointInfo info : engine.getCalls(getNodeNumber(), getCoreNumber())) {
			if (labelsMatch(info.getLabel(), label)) {
				addPc(info.getPc(), info.getLabel());

				Map<Long, CodePointTableInfo> callTable = engine.getCallTable(getNodeNumber(), getCoreNumber());
				if (!callTable.containsKey(info.getPc()))
					return false;

				if (!found) {
				    CodePointTableInfo call = callTable.get(info.getPc());
					setFileName(call.getFileName());
		        	setLineNum(call.getLineNumber());
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
		return "Call";
	}

	public String description() {
		return "Call specified using the source level pragma mechanism";
	}

	public boolean isPortable() {
		return true;
	}
}
