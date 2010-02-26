package com.xmos.xta.core.engine.references.types;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.FileNameMatcher;
import com.xmos.xta.core.engine.sections.CodePointTableInfo;

public class EndpointFileLineReference extends ReferenceType {
    EndpointFileLineReference(String ref) {
		super(ref, Type.ENDPOINT);
	}

	public ReferenceType create(Engine engine, String reference) {
		if (checkTypeString(reference, Type.ENDPOINT, 2) &&
		    isSourceReference(reference) &&
			inCodePointTable(reference, engine.getEndpointTable(), engine.getSourceMapper())) {
	    	return new EndpointFileLineReference(reference);
		}
		return null;
	}

	public boolean resolve(Engine engine) {
    	String ref  = getRef();
		String fileName = getFileName(ref);
        long lineNumber = getLineNumber(ref);

        boolean found = false;
		for (CodePointTableInfo info : engine.getEndpointTable().values()) {
            if (FileNameMatcher.isMatch(info.getCompilationDir(), info.getFileName(), fileName, engine.getSourceMapper()) &&
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
