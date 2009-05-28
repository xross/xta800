package com.xmos.xta.engine.references;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.sections.CodePointTableInfo;

public class CallFileLineReference extends Reference {
    public static final String TYPE = "CALL";
	
	CallFileLineReference(String ref) {
		super(ref);
	}

	public boolean resolve(Engine engine, int nodeId, int coreId) {
    	String ref  = getRef();
		String fileName = ReferenceFactory.getFileName(ref);
        int lineNumber  = ReferenceFactory.getLineNumber(ref);		

		for (CodePointTableInfo info : engine.getCallTable().values()) {
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
		return "CallFileLine";
	}
	
	public String description() {
		return "Call specified in the format: 'file name:line number'";
	}
}
