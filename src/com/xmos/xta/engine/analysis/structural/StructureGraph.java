package com.xmos.xta.engine.analysis.structural;

import java.util.ArrayList;
import java.util.Collection;

import com.xmos.xta.engine.analysis.structural.nodes.DefaultNodeTraverser;
import com.xmos.xta.engine.analysis.structural.nodes.LoopNode;
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

	public boolean setLoopIterations(Reference reference, long iterations) {
		return false;
	}

	public boolean unsetLoopIterations(Reference reference) {
		return false;
	}
	
	public boolean setLoopMaxIterations(Reference reference, long iterations) {
		Collection<Node> nodes = getLoopNodes(reference);
		if (nodes.size() > 0) {
		    for (Node node : nodes) {
			    ((LoopNode)node).setNumIterations(iterations);
		    }
		    return true;
		}
		return false;
	}

	public boolean unsetLoopMaxIterations(Reference reference) {
		Collection<Node> nodes = getLoopNodes(reference);
		if (nodes.size() > 0) {
		    for (Node node : nodes) {
			    ((LoopNode)node).setNumIterationsSet(false);
		    }
		    return true;
		}
		return false;
	}
	
	public boolean setLoopScope(Reference reference, boolean isAbsolute) {
		Collection<Node> nodes = getLoopNodes(reference);
		if (nodes.size() > 0) {
		    for (Node node : nodes) {
			    ((LoopNode)node).setScope(isAbsolute);
		    }
		    return true;
		}
		return false;
	}
	
	private Collection<Node> getLoopNodes(Reference reference) {
		Collection<Node> nodes = getLoopNodesContainingPc(reference.getPc());
		return checkBackTrail(nodes, reference);
	}
	
	private Collection<Node> getLoopNodesContainingPc(final int pc) {
		final Collection<Node> loopNodes = new ArrayList<Node>();		
		for (Node threadNode : m_threads) {					
			threadNode.traverse(0, new DefaultNodeTraverser() {
				public boolean foundLoopNode(Node node, int depth) {
					if (node.getPCs().contains(pc)) {
						loopNodes.add(node);
					}
					return true;
				}
		    });
		}
		return loopNodes;
	}
	
	private Collection<Node> checkBackTrail(Collection<Node> nodes, Reference reference) {
		return nodes;
	}
}
