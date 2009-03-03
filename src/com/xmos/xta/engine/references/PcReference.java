package com.xmos.xta.engine.references;

import com.xmos.xta.engine.Engine;

public class PcReference extends CodeReference {	
	PcReference(String ref) {
		super(ref);		
		setPc(CodeReferenceFactory.getPcString(ref));
	}

	public boolean resolve(Engine engine, int nodeId, int coreId) {
		if (engine.getXeObject().getPcToFunctionMap().containsKey(getPc())) {
			setResolved(true);
			return true;
		}		
		return false;
	}
}
