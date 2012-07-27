package com.xmos.xta.core.engine.references.types;

import java.util.Set;

import com.xmos.libs.core.controlflow.functionmapper.FunctionMapper;
import com.xmos.libs.core.utils.CoreId;
import com.xmos.xta.core.engine.Engine;

public class EndpointPcReference extends ReferenceType {
	EndpointPcReference(String ref, CoreId coreId) {
		super(ref, Type.ENDPOINT, coreId);
		if (ref != null) {
		    addPc(getPcString(ref));
		}
	}

	public EndpointPcReference(String ref, Set<Long> pcs, CoreId coreId) {
		super(ref, Type.ENDPOINT, coreId);
		for (long pc : pcs) {
		    addPc(pc);
		}
	}

	public ReferenceType create(Engine engine, String reference, CoreId coreId) {
		if (checkTypeString(reference, Type.ENDPOINT, 1) &&
		    isPcString(reference)) {
			return new EndpointPcReference(reference, coreId);
		}
		return null;
	}

	public Result resolve(Engine engine) {
		FunctionMapper functionMapper = engine.getFunctionMapper(getCoreId());
		if (functionMapper == null)
			return Result.UNRESOLVED;

		if (functionMapper.areProfilablePcs(getPcs())) {
	        setResolved(true);
	        return Result.RESOLVED;
	    }
	    return Result.UNRESOLVED;
	}

	public String name() {
		return "EndpointPc";
	}

	public String description() {
		return "Raw program counter specified in the format: 0x*";
	}

	public boolean isPortable() {
		return false;
	}
}
