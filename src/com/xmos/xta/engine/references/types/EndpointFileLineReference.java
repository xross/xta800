package com.xmos.xta.engine.references.types;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.sections.CodePointTableInfo;

public class EndpointFileLineReference extends ReferenceType {
    EndpointFileLineReference(String ref) {
		super(ref, Type.ENDPOINT);
	}

	public ReferenceType create(Engine engine, String reference) {
		if (checkTypeString(reference, Type.ENDPOINT, 2) &&
		    isSourceReference(reference) && 
			inCodePointTable(reference, engine.getEndpointTable())) {
	    	return new EndpointFileLineReference(reference);
		}
		return null;
	}

	public boolean resolve(Engine engine, int nodeId, int coreId) {
    	String ref  = getRef();
		String fileName = getFileName(ref);
        int lineNumber  = getLineNumber(ref);		

		for (CodePointTableInfo info : engine.getEndpointTable().values()) {
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
		return "EndpointFileLine";
	}
	
	public String description() {
		return "Endpoint specified in the format: 'file name:line number'";
	}
}
