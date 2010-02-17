package com.xmos.xta.core.engine.references.types;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.FileNameMatcher;
import com.xmos.xta.core.engine.sections.SrcLabelTableInfo;

public class SrcLabelFileLineReference extends ReferenceType {
	SrcLabelFileLineReference(String ref) {
		super(ref, Type.SRCLABEL);
	}

	public ReferenceType create(Engine engine, String reference) {
		if (checkTypeString(reference, Type.SRCLABEL, 2) &&
		    isSourceReference(reference) && 
			inSrcLabelTable(reference, engine.getSrcLabelTable(), engine.getSourceMapper())) {
	    	return new SrcLabelFileLineReference(reference);
		}
	    return null;
	}
	
	public boolean resolve(Engine engine) {
    	String ref  = getRef();
		String fileName = getFileName(ref);
        long lineNumber = getLineNumber(ref);

		for (SrcLabelTableInfo info : engine.getSrcLabelTable().values()) {
            if (FileNameMatcher.isMatch(info.getCompilationDir(), info.getFileName(), fileName, engine.getSourceMapper()) &&
            	((lineNumber >= info.getLineNumberStart()) &&
            	 (lineNumber <= info.getLineNumberEnd()))) {
            	
            	addPc(info.getPc());
            	setFileName(fileName);
            	setLineNum(lineNumber);
            	setResolved(true);
            	return true;
            }			
		}		
		return false;
	}

	public String name() {
		return "SrcLabelFileLine";
	}
	
	public String description() {
		return "Source label specified in the format: 'file name:line number'";
	}	
	
	public boolean isPortable() {
		return false;
	}	

	public boolean matches(String s, Engine engine) {
		return fileLineMatches(s, engine);
	}
}
