package com.xmos.xta.engine.references.types;

import java.util.HashMap;

import com.xmos.xeobject.Function;
import com.xmos.xeobject.XeObject;
import com.xmos.xta.engine.Engine;

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
	
	public boolean resolve(Engine engine, int nodeId, int coreId) {
		XeObject xeObject = engine.getXeObject();
		if (xeObject == null)
			return false;
		
		HashMap<Integer, Function> pcToFunctionMap = xeObject.getPcToFunctionMap();
		if (pcToFunctionMap == null)
			return false;

		if (pcToFunctionMap.containsKey(getPc())) {
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
