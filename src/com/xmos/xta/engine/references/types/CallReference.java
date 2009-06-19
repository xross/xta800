package com.xmos.xta.engine.references.types;

import java.util.Map;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.sections.CodePointInfo;
import com.xmos.xta.engine.sections.CodePointTableInfo;

public class CallReference extends ReferenceType {
	CallReference(String ref) {
		super(ref, Type.CALL);
	}

	public ReferenceType create(Engine engine, String reference) {
		if (checkTypeString(reference, Type.CALL, 1) &&
			engine.getCalls().containsKey(getLabel(reference))) {
			return new CallReference(reference);
		}	
		return null;
	}
	
	public boolean resolve(Engine engine, int nodeId, int coreId) {
		String ref = getRef();
		String label = getLabel(ref);
		
		if (engine.getCalls().containsKey(label)) {			
			CodePointInfo info = engine.getCalls().get(label);
			setPc(info.getPc());
			
			Map<Integer, CodePointTableInfo> callTable = engine.getCallTable();
			if (!callTable.containsKey(info.getPc()))
				return false;
				
		    CodePointTableInfo call = callTable.get(info.getPc());			
			setFileName(call.getFileName());
        	setLineNum(call.getLineNumber());

			setResolved(true);
			return true;
		}
		return false;
	}

	public String name() {
		return "Call";
	}
	
	public String description() {
		return "Call specified using the source level pragma mechanism";
	}	
}
