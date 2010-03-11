package com.xmos.xta.core.engine.analysis.structural.nodes.unknowns;

import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.UnknownFactory.Type;
import com.xmos.xta.core.engine.references.Reference;

public class LoopUnknown extends Unknown {
	LoopUnknown(Route route, Node node, Type type, State state, Source source, Reference[] references) {
		super(route, node, type, state, source, references);
	}

	public String getDescription() {
		switch(getState()) {
		case UNKNOWN:
			return String.format("Loop iterations (node id: " +
					getNode().getId() + ") are unknown");

		case KNOWN:
			return String.format("Loop iterations (node id: " +
					getNode().getId() + ") are set to " + 
					getNode().getAttributes().getLoopIterations());
		}
		assert(false);
		return "";
	}

	public String getScriptStr(String routeIdStr, String[] refStrs) throws Exception {
    	if (getSource() != Source.SET) {
    		throw new Exception("No script command avaliable (Not explicitly set by user)");
    	}
    	return String.format("set loop %s %s %d\n",
	    		routeIdStr, refStrs[0],
	    		getNode().getAttributes().getLoopIterations());
	}

    public String getValueStr() {
    	return String.format("%d", getNode().getAttributes().getLoopIterations());
    }	
}
