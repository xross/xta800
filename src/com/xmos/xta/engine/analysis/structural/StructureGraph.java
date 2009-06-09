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
		removeLinks();
	}

	public Collection<Node> getFunctions() {
		return m_functions;
	}

	public Collection<Node> getThreads() {
		return m_threads;
	}

	public void removeLinks() {	
		for (Node node : getThreads()) {
		    node.traverse(0, new DefaultNodeTraverser() {
		    	public boolean foundBlockNode(Node node, int depth) {
		    		node.freeMemory();
		        	return true;
		    	}
		        public boolean foundFunctionNode(Node node, int depth) {
		        	node.freeMemory();
		        	return true;
		        }
		        public boolean foundInstructionNode(Node node, int depth) {
		        	node.freeMemory();
		        	return true;
		        }
		        public boolean foundPathNode(Node node, int depth) {
		        	node.freeMemory();
		        	return true;
		        }
		        public boolean foundLoopNode(Node node, int depth) {
		        	node.freeMemory();
		        	return true;
		        }
		        public boolean foundSectionNode(Node node, int depth) {
		        	node.freeMemory();
		        	return true;
		        }
		        public boolean foundTerminalNode(Node node, int depth) {
		        	node.freeMemory();
		        	return true;
		        }
		    });	   
		}	
	}	
	
	public boolean setLoopIterations(Reference reference, long iterations) {
		boolean found = false;
		for (Node node : getReferencedNodes(reference)) {						
			Node enclosingLoopNode = getEncolsingLoopNode(node);
			if (enclosingLoopNode != null) {				
				enclosingLoopNode.setNumIterationsDone(false);
				node.setNumIterations(iterations);
				found = true;
			}
		}		
		return found;
	}

	public boolean unsetLoopIterations(Reference reference) {
		// TODO
		return false;
	}

	public boolean setLoopScope(Reference reference, boolean isAbsolute) {
		boolean found = false;
	    for (Node node : getReferencedNodes(reference)) {
			Node enclosingLoopNode = getEncolsingLoopNode(node);
			if (enclosingLoopNode != null) {				
				enclosingLoopNode.setIsAbsolute(isAbsolute);
				found = true;
			}
		}		
		return found;
	}
	
	public boolean setInstructionTimeTaken(Reference reference, double timeTaken) {
		boolean found = false;
	    for (Node node : getReferencedNodes(reference)) {
	    	node.setInstructionTimeTaken(timeTaken);
	    	found = true;
		}
		return found;
	}
	
	public boolean unsetInstructionTimeTaken(Reference reference) {
		boolean found = false;
	    for (Node node : getReferencedNodes(reference)) {
	    	node.unsetInstructionTimeTaken();
	    	found = true;
		}
		return found;
	}
	
	public boolean setInstructionNumCycles(Reference reference, long numCycles) {
		boolean found = false;
	    for (Node node : getReferencedNodes(reference)) {
	    	node.setInstructionNumCycles(numCycles);
	    	found = true;
		}
		return found;
	}
	
	public boolean unsetInstructionNumCycles(Reference reference) {
		boolean found = false;
        for (Node node : getReferencedNodes(reference)) {
        	node.unsetInstructionNumCycles();
	    	found = true;
		}
		return found;
	}
	
	public double getInstructionTimeTaken(Reference reference) {
		// TODO
		return 0;
	}
	
	public long getInstructionNumCycles(Reference reference) {
		// TODO
		return 0;
	}
	
	public boolean canInstructionPause(Reference reference) {
		// TODO
		return false;
	}
	
	private Collection<Node> getReferencedNodes(Reference reference) {
		ReferenceMatcher referenceMatcher = new ReferenceMatcher();		
		Collection<Node> referencedNodes = new ArrayList<Node>();
		for (Node threadNode : getThreads()) {
			referencedNodes.addAll(referenceMatcher.getNodes(threadNode, reference));
		}
		return referencedNodes;		
	}
	
	private Node getEncolsingLoopNode(Node node) {
		return node.isLoopNode() ? node : node.getEnclosingLoopNode();		
	}
}
