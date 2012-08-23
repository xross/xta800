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
		assert(getState() == State.KNOWN);
		Node condNode = getNode().getEnclosingCondNode();
		return String.format("Loop path iterations (node id: " +
				getNode().getId() + ") are set to " + 
				condNode.getAttributes(false).getSetLoopPathIterations(getNode()));
	}

	public String getScriptStr(String routeIdStr, String[] refStrs) {
    	assert(getSource() == Source.SET);
		Node condNode = getNode().getEnclosingCondNode();
		return String.format("set looppath %s %s %d",
    			routeIdStr, refStrs[0],
    			condNode.getAttributes(false).getSetLoopPathIterations(getNode()));
	}
	
    public String getValueStr() {
		Node condNode = getNode().getEnclosingCondNode();
    	return String.format("%d", condNode.getAttributes(false).getSetLoopPathIterations(getNode()));
    }		
}
