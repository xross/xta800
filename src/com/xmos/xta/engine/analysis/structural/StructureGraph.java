package com.xmos.xta.engine.analysis.structural;

import java.util.Collection;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.UntypedNodeTraverser;

public class StructureGraph {
	private Collection<Node> m_threads;
	
	public StructureGraph(Collection<Node> threads) {
		super();
		assert(threads.size() > 0);
		m_threads = threads;
		removeLinks();
	}

	public Collection<Node> getThreads() {
		return m_threads;
	}
	
	public void removeLinks() {	
		for (Node node : getThreads()) {
		    node.traverse(0, new UntypedNodeTraverser() {
				public boolean foundNode(Node node, int depth) {
					node.freeMemory();
					return true;
				}
		    });
		}	
	}		
}
