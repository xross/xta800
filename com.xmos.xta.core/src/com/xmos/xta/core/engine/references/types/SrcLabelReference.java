package com.xmos.xta.core.engine.references.types;

import com.xmos.libs.util.CoreId;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.sections.CodePointInfo;

public class SrcLabelReference extends ReferenceType {
	SrcLabelReference(String ref, CoreId coreId) {
		super(ref, Type.SRCLABEL, coreId);
	}

	public ReferenceType create(Engine engine, String reference, CoreId coreId) {
		if (checkTypeString(reference, Type.SRCLABEL, 1) &&
		    isValid(engine.getSrcLabels(coreId), getLabel(reference))) {
			return new SrcLabelReference(reference, coreId);
		}
		return null;
	}

	public boolean resolve(Engine engine) {
		String ref = getRef();
		String label = getLabel(ref);

		boolean found = false;
		for (CodePointInfo info : engine.getSrcLabels(getCoreId())) {
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
