package com.xmos.xta.core.engine.references.types;

import com.xmos.libs.controlflow.functionmapper.FunctionMapper;
import com.xmos.libs.util.CoreId;
import com.xmos.xta.core.engine.Engine;

public class SrcLabelPcReference extends ReferenceType {
	SrcLabelPcReference(String ref, CoreId coreId) {
		super(ref, Type.SRCLABEL, coreId);
		if (ref != null) {
		    addPc(getPcString(ref));
		}
	}

	public ReferenceType create(Engine engine, String reference, CoreId coreId) {
		if (checkTypeString(reference, Type.SRCLABEL, 1) &&
		    isPcString(reference)) {
			return new SrcLabelPcReference(reference, coreId);
		}
		return null;
	}

	public boolean resolve(Engine engine) {
		FunctionMapper functionMapper = engine.getFunctionMapper(getCoreId());
		if (functionMapper == null)
			return false;

		if (functionMapper.areProfilablePcs(getPcs())) {
	        setResolved(true);
	        return true;
	    }
	    return false;
	}

	public String name() {
		return "SrcLabelPc";
	}

	public String description() {
		return "Raw program counter specified in the format: 0x*";
	}

	public boolean isPortable() {
		return false;
	}
}
