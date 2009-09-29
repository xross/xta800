package com.xmos.xta.core.engine.analysis.structural;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.UntypedNodeTraverser;

public class StructureGraph {
	private Collection<FunctionNode> m_threads;
	private Collection<FunctionNode> m_functions;
	private Collection<FunctionNode> m_unlinkedFunctions;
	private Node m_searchNode;
	private Map<Long, TreeSet<Node>> m_pcToEndPoints;
	
	public StructureGraph(Collection<FunctionNode> threads, Collection<FunctionNode> functions, Map<Long, TreeSet<Node>> pcToEndPoints) {
		super();
		m_threads = threads;
		m_functions = functions;
		m_pcToEndPoints = pcToEndPoints;
		m_unlinkedFunctions = new TreeSet<FunctionNode>();
		removeLinks();
	}

	public Map<Long, TreeSet<Node>> getPcToEndPoints(){
		return m_pcToEndPoints;
	}
	
	public Collection<FunctionNode> getThreads() {
		return m_threads;
	}
	
	public Collection<FunctionNode> getFunctions() {
		return m_functions;
	}
	
	public Collection<FunctionNode> getUnlinkedFunctions() {
		return m_unlinkedFunctions;
	}
	
	public void removeLinks() {	
		for (Node node : getThreads()) {
		    node.traverse(new Stack<Node>(), new UntypedNodeTraverser() {
				public boolean foundNode(Node node, Stack<Node> ancestors) {
					node.freeMemory();
					return true;
				}
		    });
		}	
		for (Node node : getUnlinkedFunctions()) {
		    node.traverse(new Stack<Node>(), new UntypedNodeTraverser() {
				public boolean foundNode(Node node, Stack<Node> ancestors) {
					node.freeMemory();
					return true;
				}
		    });
		}	
	}
	
    public Node getNode(final int nodeId) {
    	m_searchNode = null;
		for (Node node : getThreads()) {					
		    node.traverse(new Stack<Node>(), new UntypedNodeTraverser() {
				public boolean foundNode(Node node, Stack<Node> ancestors) {
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
        
        for (Node node : getUnlinkedFunctions()) {					
		    node.traverse(new Stack<Node>(), new UntypedNodeTraverser() {
				public boolean foundNode(Node node, Stack<Node> ancestors) {
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

	public void setUnlinkedFunctions(Set<FunctionNode> unlinkedFunctions) {
		m_unlinkedFunctions = unlinkedFunctions;
	}		
}
