package com.xmos.xta.core.engine.analysis.structural.nodes.unknowns;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.UnknownFactory.Type;
import com.xmos.xta.core.engine.references.Reference;

public class LoopScopeUnknown extends Unknown {
	LoopScopeUnknown(Node node, Type type, State state, Source source, Reference[] references) {
		super(node, type, state, source, references);
	}

	public String getDescription() {
		switch(getState()) {
		case UNKNOWN:
			return String.format("Loop scope (node id: " + 
					getNode().getId() + ") has not been set");

		case KNOWN:
			return String.format("Loop scope (node id: " + 
					getNode().getId() + ") has been set");
		}
		assert(false);
		return "";
	}

	public String getScriptStr(String routeIdStr, String[] refStrs) throws Exception {
    	if (getSource() == Source.IMPLICIT) {
    		throw new Exception("No script command avaliable (Not explicitly set by user)");
    	}
    	return String.format("set loopscope %s %s %s\n", 
    			routeIdStr, refStrs[0], 
    			getNode().getAttributes().getIsAbsolute() ? "a" : "r");
	}
}
