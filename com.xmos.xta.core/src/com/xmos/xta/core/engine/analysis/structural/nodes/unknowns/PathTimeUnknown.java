package com.xmos.xta.core.engine.analysis.structural.nodes.unknowns;

import com.xmos.libs.util.Conversion;
import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.UnknownFactory.Type;
import com.xmos.xta.core.engine.references.Reference;

public class PathTimeUnknown extends Unknown {
	PathTimeUnknown(Route route, Node node, Type type, State state, Source source, Reference[] references) {
		super(route, node, type, state, source, references);
	}

	public String getDescription() {
		switch(getState()) {
		case UNKNOWN:
			return String.format("Time taken for path (node id: %d) is unknown",
					getNode().getId());

		case KNOWN:
			return String.format("Time taken for path (node id: %d) is set to %s",
					getNode().getId(),
					Conversion.getTimeString(getNode().getAttributes().getFixedTime().getNs()));
		}
		assert(false);
		return "";
	}

	public String getScriptStr(String routeIdStr, String[] refStrs) throws Exception {
    	if (getSource() != Source.SET) {
    		throw new Exception("No script command avaliable (Not explicitly set by user)");
    	}
    	return String.format("set pathtime %s %s %s %s",
    			routeIdStr, refStrs[0], refStrs[1],
    			getNode().getAttributes().getFixedTimeString());
	}

    public String getValueStr() {
    	return getNode().getAttributes().getFixedTimeString();
    }	
}