package com.xmos.xta.core.engine.references.types;

import com.xmos.libs.util.FileNameCompare;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.sections.CodePointTableInfo;

public class CallFileLineReference extends ReferenceType {
	CallFileLineReference(String ref) {
		super(ref, Type.CALL);
	}

	public ReferenceType create(Engine engine, String reference) {
		if (checkTypeString(reference, Type.CALL, 2) &&
		    isSourceReference(reference) && 
			inCodePointTable(reference, engine.getCallTable())) {
	    	return new CallFileLineReference(reference);	
		}	
		return null;
	}
	
	public boolean resolve(Engine engine, int nodeId, int coreId) {
    	String ref  = getRef();
		String fileName = getFileName(ref);
        long lineNumber = getLineNumber(ref);		

		for (CodePointTableInfo info : engine.getCallTable().values()) {
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
		return "CallFileLine";
	}
	
	public String description() {
		return "Call specified in the format: 'file name:line number'";
	}
}
