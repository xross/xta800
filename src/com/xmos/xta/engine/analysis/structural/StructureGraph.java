package com.xmos.xta.engine.analysis.structural;

import java.util.ArrayList;
import java.util.Collection;

import com.xmos.xta.engine.analysis.structural.nodes.DefaultNodeTraverser;
import com.xmos.xta.engine.analysis.structural.nodes.LoopNode;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.instructions.XtaInstruction;
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
		boolean loopIterationsSet = false;
		
		Collection<Node> referencedNodes = getReferencedNodes(reference);		
		for (Node referencedNode : referencedNodes) {
						
			Node enclosingLoopNode = referencedNode.isLoopNode() ? 
					referencedNode : 
					referencedNode.getEnclosingLoopNode();
								
			if (enclosingLoopNode != null) {				
				enclosingLoopNode.setNumIterationsDone(false);
				referencedNode.setNumIterations(iterations);
				loopIterationsSet = true;
			}
		}
		return loopIterationsSet;
	}
		
	public boolean unsetLoopIterations(Reference reference) {
		return false;
	}

	public boolean setLoopScope(Reference reference, boolean isAbsolute) {
		Collection<Node> nodes = getReferencedNodes(reference);
		if (nodes.size() > 0) {
		    for (Node node : nodes) {
			    ((LoopNode)node).setScope(isAbsolute);
		    }
		    return true;
		}
		return false;
	}
	
	private Collection<Node> getReferencedNodes(Reference reference) {
		Collection<Node> nodes = getNodesContainingPc(reference.getPc());
		return checkBackTrail(nodes, reference);
	}
		
	private Collection<Node> getNodesContainingPc(final int pc) {
		final Collection<Node> nodes = new ArrayList<Node>();		
		for (Node threadNode : m_threads) {				
			threadNode.traverse(0, new DefaultNodeTraverser() {
				public boolean foundBlockNode(Node node, int depth) {
					for (XtaInstruction instruction : node.getInstructions()) {
						if (instruction.getPc() == pc) {
							nodes.add(node);
							break;
						}
					}
					return true;
				}
		    });
		}
		return nodes;
	}

	private Collection<Node> checkBackTrail(Collection<Node> nodes, Reference reference) {
		return nodes;
	}
}
