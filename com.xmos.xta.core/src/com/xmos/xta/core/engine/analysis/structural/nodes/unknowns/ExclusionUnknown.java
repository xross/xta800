package com.xmos.xta.core.engine.analysis.structural.nodes.unknowns;

import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.UnknownFactory.Type;
import com.xmos.xta.core.engine.references.Reference;

public class ExclusionUnknown extends Unknown {
	ExclusionUnknown(Route route, Node node, Type type, State state, Source source, Reference[] references) {
		super(route, node, type, state, source, references);
	}

	public String getDescription() {
		switch(getState()) {
		case KNOWN:
			return String.format("Exclusion on node (node id: %d)", getNode().getId());
		}
		assert(false);
		return "";
	}

	public String getScriptStr(String routeIdStr, String[] refStrs) throws Exception {
    	if (getSource() != Source.SET) {
    		throw new Exception("No script command avaliable (Not explicitly set by user)");
    	}
    	return String.format("set exclusion %s %s", routeIdStr, refStrs[0]);
	}

	public String getValueStr() {
		return "";
	}
}
