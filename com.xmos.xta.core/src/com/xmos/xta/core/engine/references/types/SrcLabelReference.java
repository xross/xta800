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

	public Result resolve(Engine engine) {
		String ref = getRef();
		String label = getLabel(ref);

		Result result = Result.UNRESOLVED;
		for (CodePointInfo info : engine.getSrcLabels(getCoreId())) {
			if (labelsMatch(info.getLabel(), label)) {
			    if (info.exists()) {
	                addPc(info.getPc(), info.getLabel());
	                setResolved(true);
	                result = Result.RESOLVED;
			        
			    } else {
			        result = Result.REMOVED;
			    }
			}
		}
		return result;
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
