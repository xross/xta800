package com.xmos.xta.core.engine.references.types;

import java.util.Collection;

import com.xmos.xta.core.engine.Engine;

public class FunctionReference extends ReferenceType {
	FunctionReference(String ref, int nodeNumber, int coreNumber) {
		super(ref, Type.FUNCTION, nodeNumber, coreNumber);
	}

	public ReferenceType create(Engine engine, String reference, int nodeNumber, int coreNumber) {
		if (checkTypeString(reference, Type.FUNCTION, 1) &&
		    engine.getLabels(getNodeNumber(), getCoreNumber()).containsKey(getLabel(reference))) {
			return new FunctionReference(reference, nodeNumber, coreNumber);
		}
		return null;
	}

	public boolean resolve(Engine engine) {
		String ref = getRef();
		String functionName = getLabel(ref);

        Collection<String> functionNames = engine.getFunctionNames(getNodeNumber(), getCoreNumber());
        if (functionNames.contains(functionName)) {
        	for(long pc : engine.getFunctionPcs(functionName, getNodeNumber(), getCoreNumber())){
        		addPc(pc);
        	}
	        setResolved(true);
	        return true;
        }
	    return false;
	}

	public String name() {
		return "Function";
	}

	public String description() {
		return "Any function";
	}

	public boolean isPortable() {
		return true;
	}

	public boolean matches(String s) {
		return getRef().equals(s);
	}
}
