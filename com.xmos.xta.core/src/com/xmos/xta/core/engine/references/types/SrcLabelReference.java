package com.xmos.xta.core.engine.references.types;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.sections.CodePointInfo;

public class SrcLabelReference extends ReferenceType {
	SrcLabelReference(String ref, int nodeNumber, int coreNumber) {
		super(ref, Type.SRCLABEL, nodeNumber, coreNumber);
	}

	public ReferenceType create(Engine engine, String reference, int nodeNumber, int coreNumber) {
		if (checkTypeString(reference, Type.SRCLABEL, 1) &&
		    isValid(engine.getSrcLabels(getNodeNumber(), getCoreNumber()), getLabel(reference))) {
			return new SrcLabelReference(reference, nodeNumber, coreNumber);
		}
		return null;
	}

	public boolean resolve(Engine engine) {
		String ref = getRef();
		String label = getLabel(ref);

		boolean found = false;
		for (CodePointInfo info : engine.getSrcLabels(getNodeNumber(), getCoreNumber())) {
			if (labelsMatch(info.getLabel(), label)) {
				addPc(info.getPc(), info.getLabel());
				found = true;
			}
		}

		if (found) {
			setResolved(true);
		}
		return found;
	}

	public String name() {
		return "SrcLabel";
	}

	public String description() {
		return "Source label specified using the source level pragma mechanism";
	}

	public boolean isPortable() {
		return true;
	}
}
