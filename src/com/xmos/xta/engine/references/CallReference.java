package com.xmos.xta.engine.references;

import java.util.Map;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.sections.CodePointInfo;
import com.xmos.xta.engine.sections.CodePointTableInfo;

public class CallReference extends Reference {
	CallReference(String ref) {
		super(ref);
	}

	public boolean resolve(Engine engine, int nodeId, int coreId) {
		String ref = getRef();
		if (engine.getCalls().containsKey(ref)) {			
			CodePointInfo info = engine.getCalls().get(ref);
			setPc(info.getPc());
			
			// Look up the call in the call table
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
