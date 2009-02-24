package com.xmos.xta.engine.references;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.maps.SrcTableInfo;

public class SrcLabelFileLineReference extends CodeReference {
	SrcLabelFileLineReference(String ref) {
		super(ref);
	}

	public boolean resolve(Engine engine, int id, int id2) {
    	String ref  = getRef();
		String fileName = CodeReferenceFactory.getFileName(ref);
        int lineNumber  = CodeReferenceFactory.getLineNumber(ref);		

		for (SrcTableInfo info : engine.getSrcLabelTable().values()) {
            if (info.getFileName().equals(fileName) &&
            	(info.getLineNumber() == lineNumber)) {
            	setPc(info.getPc());
            	return true;
            }			
		}		
		return false;
	}
}
