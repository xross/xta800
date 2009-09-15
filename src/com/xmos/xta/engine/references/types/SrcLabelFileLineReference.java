package com.xmos.xta.engine.references.types;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.sections.SrcLabelTableInfo;

public class SrcLabelFileLineReference extends ReferenceType {
	SrcLabelFileLineReference(String ref) {
		super(ref, Type.SRCLABEL);
	}

	public ReferenceType create(Engine engine, String reference) {
		if (checkTypeString(reference, Type.SRCLABEL, 2) &&
		    isSourceReference(reference) && 
			inSrcLabelTable(reference, engine.getSrcLabelTable())) {
	    	return new SrcLabelFileLineReference(reference);
		}
	    return null;
	}
	
	public boolean resolve(Engine engine, int nodeId, int coreId) {
    	String ref  = getRef();
		String fileName = getFileName(ref);
        long lineNumber = getLineNumber(ref);

		for (SrcLabelTableInfo info : engine.getSrcLabelTable().values()) {
            if (info.getFileName().equals(fileName) &&
            	((lineNumber >= info.getLineNumberStart()) &&
            	 (lineNumber <= info.getLineNumberEnd()))) {
            	
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
		return "SrcLabelFileLine";
	}
	
	public String description() {
		return "Source label specified in the format: 'file name:line number'";
	}	
}
