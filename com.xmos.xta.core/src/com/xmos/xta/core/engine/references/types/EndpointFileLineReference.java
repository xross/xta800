package com.xmos.xta.core.engine.references.types;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.sections.CodePointTableInfo;
import com.xmos.xta.core.engine.utils.FileNameMatcher;

public class EndpointFileLineReference extends ReferenceType {
    EndpointFileLineReference(String ref, int nodeNumber, int coreNumber) {
		super(ref, Type.ENDPOINT, nodeNumber, coreNumber);
	}

	public ReferenceType create(Engine engine, String reference, int nodeNumber, int coreNumber) {
		if (checkTypeString(reference, Type.ENDPOINT, 2) &&
		    isSourceReference(reference) &&
			inCodePointTable(reference, engine.getEndpointTable(getNodeNumber(), getCoreNumber()), engine.getSourceMapper(getNodeNumber(), getCoreNumber()))) {
	    	return new EndpointFileLineReference(reference, nodeNumber, coreNumber);
		}
		return null;
	}

	public boolean resolve(Engine engine) {
    	String ref  = getRef();
		String fileName = getFileName(ref);
        long lineNumber = getLineNumber(ref);

        boolean found = false;
		for (CodePointTableInfo info : engine.getEndpointTable(getNodeNumber(), getCoreNumber()).values()) {
            if (FileNameMatcher.isMatch(info.getCompilationDir(), info.getFileName(), fileName, engine.getSourceMapper(getNodeNumber(), getCoreNumber())) &&            		
            	(info.getLineNumber() == lineNumber)) {
            	
            	addPc(info.getPc());
            	setFileName(fileName);
            	setLineNum(lineNumber);
            	found = true;
            }
		}

		if (found) {
			setResolved(true);
		}
		return found;
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

	public boolean matches(String s, Engine engine) {
		return fileLineMatches(s, engine);
	}
}
