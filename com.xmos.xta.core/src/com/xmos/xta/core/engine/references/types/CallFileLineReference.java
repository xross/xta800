package com.xmos.xta.core.engine.references.types;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.FileNameMatcher;
import com.xmos.xta.core.engine.sections.CodePointTableInfo;

public class CallFileLineReference extends ReferenceType {
	CallFileLineReference(String ref) {
		super(ref, Type.CALL);
	}

	public ReferenceType create(Engine engine, String reference) {
		if (checkTypeString(reference, Type.CALL, 2) &&
		    isSourceReference(reference) && 
			inCodePointTable(reference, engine.getCallTable(), engine.getSourceMapper())) {
	    	return new CallFileLineReference(reference);	
		}	
		return null;
	}
	
	public boolean resolve(Engine engine) {
    	String ref  = getRef();
		String fileName = getFileName(ref);
        long lineNumber = getLineNumber(ref);		

        boolean found = false;
		for (CodePointTableInfo info : engine.getCallTable().values()) {
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
		return "CallFileLine";
	}
	
	public String description() {
		return "Call specified in the format: 'file name:line number'";
	}
	
	public boolean isPortable() {
		return false;
	}
	
	public boolean matches(String s, Engine engine) {
		return fileLineMatches(s, engine);
	}
}
