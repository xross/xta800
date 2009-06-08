package com.xmos.xta.engine.references.types;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.sections.CodePointTableInfo;

public class CallFileLineReference extends ReferenceType {
	CallFileLineReference(String ref) {
		super(ref, Type.CALL);
	}

	public ReferenceType create(Engine engine, String reference) {
		if (checkTypeString(reference, Type.CALL) &&
		    isSourceReference(reference) && 
			inCodePointTable(reference, engine.getCallTable())) {
	    	return new CallFileLineReference(reference);	
		}	
		return null;
	}
	
	public boolean resolve(Engine engine, int nodeId, int coreId) {
    	String ref  = getRef();
		String fileName = getFileName(ref);
        int lineNumber  = getLineNumber(ref);		

		for (CodePointTableInfo info : engine.getCallTable().values()) {
            if (info.getFileName().equals(fileName) &&
            	(info.getLineNumber() == lineNumber)) {
            	setPc(info.getPc());
            	setFileName(fileName);
            	setLineNum(lineNumber);

    			setResolved(true);
    			return true;
            }			
		}		
		return false;
	}		      

	public String name() {
		return "CallFileLine";
	}
	
	public String description() {
		return "Call specified in the format: 'file name:line number'";
	}
}
