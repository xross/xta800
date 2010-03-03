package com.xmos.xta.core.engine.references.types;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.sourcemapper.SourceMapper;

public class PcReference extends ReferenceType {
	PcReference(String ref, int nodeNumber, int coreNumber) {
		super(ref, Type.PC, nodeNumber, coreNumber);
		if (ref != null) {
		    addPc(getPcString(ref));
		}
	}

	public ReferenceType create(Engine engine, String reference, int nodeNumber, int coreNumber) {
		if (checkTypeString(reference, Type.PC, 1) &&
		    isPcString(reference)) {
			return new PcReference(reference, nodeNumber, coreNumber);
		}
		return null;
	}

	public boolean resolve(Engine engine) {
		SourceMapper sourceMapper = engine.getSourceMapper(getNodeNumber(), getCoreNumber());
		if (sourceMapper == null)
			return false;

		if (sourceMapper.areProfilablePcs(getPcs())) {
	        setResolved(true);
	        return true;
	    }
	    return false;
	}

	public String name() {
		return "Pc";
	}

	public String description() {
		return "Raw program counter specified in the format: 0x*";
	}

	public boolean isPortable() {
		return false;
	}
}
