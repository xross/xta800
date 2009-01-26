package com.xmos.xta.engine.references;

import com.xmos.xta.engine.Engine;

public class PcReference extends CodeReference {
	
	PcReference(String ref, int pc) {
		super(ref);		
		
		//TODO make these bounds refer to the memory size
		if (pc >= 0x10000 && pc < 0x20000){
			setResolved(true);
		} else {
			setResolved(false);
		}
		setPc(pc);
	}

	public boolean resolve(Engine engine, int id, int id2) {
		// Need to check for existence within the executable
		return engine.getXeObject().getPcToFunctionMap().containsKey(getPc());
	}
}
