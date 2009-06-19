package com.xmos.xta.engine.analysis.structural;

import java.util.Collection;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.UntypedNodeTraverser;

public class StructureGraph {
	private Collection<Node> m_threads;
	private Node m_searchNode;
	
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
	
    public Node getNode(final int nodeId) {
    	m_searchNode = null;
		for (Node node : getThreads()) {					
		    node.traverse(0, new UntypedNodeTraverser() {
				public boolean foundNode(Node node, int depth) {
					if (node.getId() == nodeId) {
						m_searchNode = node;
						return false;
					}
					return true;
				}
		    });
		}
        return m_searchNode;   	
    }		
}
