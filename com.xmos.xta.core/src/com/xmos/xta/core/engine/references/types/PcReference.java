package com.xmos.xta.core.engine.references.types;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.sourcemapper.SourceMapper;

public class PcReference extends ReferenceType {	
	PcReference(String ref) {
		super(ref, Type.PC);				
		if (ref != null) {
		    setPc(getPcString(ref));
		}
	}

	public ReferenceType create(Engine engine, String reference) {
		if (checkTypeString(reference, Type.PC, 1) &&
		    isPcString(reference)) {
			return new PcReference(reference);
		}
		return null;
	}
	
	public boolean resolve(Engine engine) {
		SourceMapper sourceMapper = engine.getSourceMapper();
		if (sourceMapper == null) 
			return false;

		if (sourceMapper.isProfilablePc(getPc())) {
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
