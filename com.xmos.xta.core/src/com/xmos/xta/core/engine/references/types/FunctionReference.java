package com.xmos.xta.core.engine.references.types;

import java.util.Collection;

import com.xmos.libs.xeloader.SymbolTable;
import com.xmos.xta.core.engine.Engine;

public class FunctionReference extends ReferenceType {
	FunctionReference(String ref) {
		super(ref, Type.FUNCTION);
	}
	
	public ReferenceType create(Engine engine, String reference) {
		if (checkTypeString(reference, Type.FUNCTION, 1) &&
		    engine.getLabels().containsKey(getLabel(reference))) {
			return new FunctionReference(reference);
		}
		return null;
	}
	
	public boolean resolve(Engine engine) {
		String ref = getRef();
		String functionName = getLabel(ref);
		
        Collection<String> functionNames = engine.getFunctionNames();
        if (functionNames.contains(functionName)) {        	        	
    	    int nodeId = engine.getNodeId();
    	    int coreId = engine.getCoreId();        	        	
            SymbolTable symbolTable = engine.getXeLoader().getSymbolTable(nodeId, coreId);            
		    setPc(symbolTable.symbolAddress(functionName));
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
