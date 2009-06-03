package com.xmos.xta.engine.references.types;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.sections.CodePointTableInfo;

public class EndpointFileLineReference extends ReferenceType {
    public static final String TYPE = "ENDPOINT";

    EndpointFileLineReference(String ref) {
		super(ref);
	}

	public boolean resolve(Engine engine, int nodeId, int coreId) {
    	String ref  = getRef();
		String fileName = ReferenceTypeFactory.getFileName(ref);
        int lineNumber  = ReferenceTypeFactory.getLineNumber(ref);		

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
