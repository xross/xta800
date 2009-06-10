package com.xmos.xta.engine.analysis.structural;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;

public class StructureGraphFnopAdder {

	public void analyse(Case c, Node graph, boolean isFunction) {
		//FIXME
		graph.updateFnops(c, 0);
	}

}
