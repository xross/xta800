package com.xmos.xta.engine.references;

import com.xmos.xta.engine.Engine;

public class PcReference extends CodeReference {	
	PcReference(String ref, int pc) {
		super(ref);		
		setPc(pc);
	}

	public boolean resolve(Engine engine, int nodeId, int coreId) {
		if (engine.getXeObject().getPcToFunctionMap().containsKey(getPc())) {
			setResolved(true);
			return true;
		}		
		return false;
	}
}
