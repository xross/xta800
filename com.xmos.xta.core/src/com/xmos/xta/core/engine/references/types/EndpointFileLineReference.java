package com.xmos.xta.core.engine.references.types;

import com.xmos.libs.util.FileNameCompare;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.sections.CodePointTableInfo;

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

	public boolean resolve(Engine engine) {
    	String ref  = getRef();
		String fileName = getFileName(ref);
        long lineNumber = getLineNumber(ref);		

		for (CodePointTableInfo info : engine.getEndpointTable().values()) {
            if (FileNameCompare.fileNamesMatch(info.getCompilationDir(), info.getFileName(), fileName) &&
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
	
	public boolean isPortable() {
		return false;
	}	
}
