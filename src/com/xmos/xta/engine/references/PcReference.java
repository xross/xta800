package com.xmos.xta.engine.references;

import com.xmos.xta.engine.Engine;

public class PcReference extends Reference {	
	PcReference(String ref) {
		super(ref);				
		if (ref != null) {
		    setPc(ReferenceFactory.getPcString(ref));
		}
	}

	public boolean resolve(Engine engine, int nodeId, int coreId) {
		if (engine.getXeObject().getPcToFunctionMap().containsKey(getPc())) {
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
}
