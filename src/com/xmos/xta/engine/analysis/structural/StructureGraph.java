package com.xmos.xta.engine.analysis.structural;

import java.util.ArrayList;
import java.util.Collection;

import com.xmos.xta.engine.analysis.structural.nodes.DefaultNodeTraverser;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.references.Reference;

public class StructureGraph {

	private Collection<Node> m_functions;
	private Collection<Node> m_threads;
	
	public StructureGraph(Collection<Node> functions, Collection<Node> threads) {
		super();
		assert(threads.size() > 0);
		m_functions = functions;
		m_threads = threads;
	}

	public Collection<Node> getFunctions() {
		return m_functions;
	}

	public Collection<Node> getThreads() {
		return m_threads;
	}
	
	public boolean setLoopMaxIterations(Reference reference, long iterations) {
		Collection<Node> nodes = getMatchingLoopNodes(reference.getPc());		
		return false;
	}
	
	private Collection<Node> getMatchingLoopNodes(final int pc) {
		final Collection<Node> nodes = new ArrayList<Node>();
		
		for (Node threadNode : m_threads) {					
			threadNode.traverse(0, new DefaultNodeTraverser() {
				public boolean foundLoopNode(Node node, int depth) {
					if (node.getPCs().contains(pc)) {
						nodes.add(node);
					}
					return true;
				}
		    });
		}
		return nodes;
	}
}
