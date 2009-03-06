package com.xmos.xta.engine.references;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.maps.SrcTableInfo;

public class SrcLabelFileLineReference extends Reference {
	SrcLabelFileLineReference(String ref) {
		super(ref);
	}

	public boolean resolve(Engine engine, int nodeId, int coreId) {
    	String ref  = getRef();
		String fileName = ReferenceFactory.getFileName(ref);
        int lineNumber  = ReferenceFactory.getLineNumber(ref);		

		for (SrcTableInfo info : engine.getSrcLabelTable().values()) {
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
		return "SrcLabelFileLine";
	}
	
	public String description() {
		return "Source label specified in the format: 'file name:line number'";
	}	
}
