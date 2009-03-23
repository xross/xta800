package com.xmos.xta.engine.references;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.maps.EndpointTableInfo;

public class EndpointFileLineReference extends Reference {
	EndpointFileLineReference(String ref) {
		super(ref);
	}

	public boolean resolve(Engine engine, int nodeId, int coreId) {
    	String ref  = getRef();
		String fileName = ReferenceFactory.getFileName(ref);
        int lineNumber  = ReferenceFactory.getLineNumber(ref);		

		for (EndpointTableInfo info : engine.getEndpointTable().values()) {
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
