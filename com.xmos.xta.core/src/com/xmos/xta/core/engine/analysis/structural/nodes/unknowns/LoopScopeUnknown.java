package com.xmos.xta.core.engine.analysis.structural.nodes.unknowns;

import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.UnknownFactory.Type;
import com.xmos.xta.core.engine.references.Reference;

public class LoopScopeUnknown extends Unknown {
	LoopScopeUnknown(Route route, Node node, Type type, State state, Source source, Reference[] references) {
		super(route, node, type, state, source, references);
	}

	public String getDescription() {
		assert(getState() == State.KNOWN);
		return String.format("Loop scope (node id: " +
				getNode().getId() + ") has been set to '" + 
				(getNode().getAttributes().getIsAbsolute() ? "a" : "r") + "'");
	}

	public String getScriptStr(String routeIdStr, String[] refStrs) {
    	assert(getSource() == Source.SET);
    	return String.format("set loopscope %s %s %s",
    			routeIdStr, refStrs[0],
    			getNode().getAttributes().getIsAbsolute() ? "a" : "r");
	}
	
    public String getValueStr() {
    	return getNode().getAttributes().getIsAbsolute() ? "a" : "r";
    }	
}
