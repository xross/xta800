package com.xmos.xta.engine.references;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.maps.SrcTableInfo;

public class EndpointFileLineReference extends Reference {
	EndpointFileLineReference(String ref) {
		super(ref);
	}

	public boolean resolve(Engine engine, int nodeId, int coreId) {
    	String ref  = getRef();
		String fileName = ReferenceFactory.getFileName(ref);
        int lineNumber  = ReferenceFactory.getLineNumber(ref);		

		for (SrcTableInfo info : engine.getEndpointTable().values()) {
            if (info.getFileName().equals(fileName) &&
            	(info.getLineNumber() == lineNumber)) {
            	setPc(info.getPc());
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
