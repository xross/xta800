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

		for (CodePointTableInfo info : engine.getCallTable().values()) {
            if (FileNameMatcher.isMatch(info.getCompilationDir(), info.getFileName(), fileName, engine.getSourceMapper()) &&
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
	
	public boolean isPortable() {
		return false;
	}
}
