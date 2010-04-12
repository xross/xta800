package com.xmos.xta.core.engine.references.types;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.sourcemapper.SourceMapper;
import com.xmos.xta.core.engine.utils.CoreId;

public class CallPcReference extends ReferenceType {
	CallPcReference(String ref, CoreId coreId) {
		super(ref, Type.CALL, coreId);
		if (ref != null) {
		    addPc(getPcString(ref));
		}
	}

	public ReferenceType create(Engine engine, String reference, CoreId coreId) {
		if (checkTypeString(reference, Type.CALL, 1) &&
			isPcString(reference)) {
	    	return new CallPcReference(reference, coreId);
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
		return "CallPc";
	}

	public String description() {
		return "Call specified in the format: 0x*";
	}

	public boolean isPortable() {
		return false;
	}
}
