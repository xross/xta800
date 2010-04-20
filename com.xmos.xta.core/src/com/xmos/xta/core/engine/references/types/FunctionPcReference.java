package com.xmos.xta.core.engine.references.types;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.sourcemapper.SourceMapper;
import com.xmos.xta.core.engine.utils.CoreId;

public class FunctionPcReference extends ReferenceType {
	FunctionPcReference(String ref, CoreId coreId) {
		super(ref, Type.FUNCTION, coreId);
		if (ref != null) {
		    addPc(getPcString(ref));
		}
	}

	public ReferenceType create(Engine engine, String reference, CoreId coreId) {
		if (checkTypeString(reference, Type.FUNCTION, 1) &&
		    isPcString(reference)) {
			return new FunctionPcReference(reference, coreId);
		}
		return null;
	}

	public boolean resolve(Engine engine) {
		SourceMapper sourceMapper = engine.getSourceMapper(getCoreId());
		if (sourceMapper == null)
			return false;

		if (sourceMapper.areProfilablePcs(getPcs())) {
	        setResolved(true);
	        return true;
	    }
	    return false;
	}

	public String name() {
		return "FunctionPc";
	}

	public String description() {
		return "Raw program counter specified in the format: 0x*";
	}

	public boolean isPortable() {
		return false;
	}
}
