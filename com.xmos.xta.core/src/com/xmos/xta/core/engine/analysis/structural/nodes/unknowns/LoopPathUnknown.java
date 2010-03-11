package com.xmos.xta.core.engine.analysis.structural.nodes.unknowns;

import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.UnknownFactory.Type;
import com.xmos.xta.core.engine.references.Reference;

public class LoopPathUnknown extends Unknown {
	LoopPathUnknown(Route route, Node node, Type type, State state, Source source, Reference[] references) {
		super(route, node, type, state, source, references);
	}

	public String getDescription() {
		switch(getState()) {
		case UNKNOWN:
			return String.format("Loop path iterations (node id: " +
					getNode().getId() + ") are unknown");

		case KNOWN:
			return String.format("Loop path iterations (node id: " +
					getNode().getId() + ") are known");
		}
		assert(false);
		return "";
	}

	public String getScriptStr(String routeIdStr, String[] refStrs) throws Exception {
    	if (getSource() != Source.SET) {
    		throw new Exception("No script command avaliable (Not explicitly set by user)");
    	}
		Node condNode = getNode().getEnclosingCondNode();
		return String.format("set looppath %s %s %d\n",
    			routeIdStr, refStrs[0],
    			condNode.getAttributes().getSetLoopPathIterations(getNode()));
	}
	
    public String getValueStr() {
		Node condNode = getNode().getEnclosingCondNode();
    	return String.format("%d", condNode.getAttributes().getSetLoopPathIterations(getNode()));
    }		
}
