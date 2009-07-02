package com.xmos.xta.engine.analysis.structural;

import java.util.Collection;

import com.xmos.xta.engine.analysis.structural.nodes.FunctionNode;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.UntypedNodeTraverser;

public class StructureGraph {
	private Collection<FunctionNode> m_threads;
	private Collection<FunctionNode> m_functions;
	private Node m_searchNode;
	
	
	public StructureGraph(Collection<FunctionNode> threads, Collection<FunctionNode> functions) {
		super();
		assert(threads.size() > 0);
		m_threads = threads;
		m_functions = functions;
		removeLinks();
	}

	public Collection<FunctionNode> getThreads() {
		return m_threads;
	}
	
	public Collection<FunctionNode> getFunctions() {
		return m_functions;
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
		for (Node node : getFunctions()) {
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
		if(m_searchNode != null){
			return m_searchNode;  
		}
        
        for (Node node : getFunctions()) {					
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
