package com.xmos.xta.core.engine.analysis.structural.nodes.unknowns;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.UnknownFactory.Type;
import com.xmos.xta.core.engine.references.Reference;

public class LoopPathUnknown extends Unknown {
	LoopPathUnknown(Node node, Type type, State state, Source source, Reference[] references) {
		super(node, type, state, source, references);
	}

	public String getDescription() {
		switch(getState()) {
		case UNKNOWN:
			return String.format("Loop path iterations on node " + 
					getNode().getId() + " are unknown");

		case KNOWN:
			return String.format("Loop path iterations on node " + 
					getNode().getId() + " are known");
		}
		assert(false);
		return "";
	}

	public String getScriptStr(String routeIdStr, String[] refStrs) throws Exception {
    	assert(getSource() == Source.EXPLICIT);
		Node condNode = getNode().getEnclosingCondNode();
		return String.format("set looppath %s %s %d\n", 
    			routeIdStr, refStrs[0], 
    			condNode.getAttributes().getSetLoopPathIterations(getNode()));    	
	}
}
