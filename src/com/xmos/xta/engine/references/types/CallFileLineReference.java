package com.xmos.xta.engine.references.types;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.sections.CodePointTableInfo;

public class CallFileLineReference extends ReferenceType {
    public static final String TYPE = "CALL";
	
	CallFileLineReference(String ref) {
		super(ref);
	}

	public boolean resolve(Engine engine, int nodeId, int coreId) {
    	String ref  = getRef();
		String fileName = ReferenceTypeFactory.getFileName(ref);
        int lineNumber  = ReferenceTypeFactory.getLineNumber(ref);		

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
