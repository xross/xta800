package com.xmos.xta.engine.analysis.structural;

import java.util.ArrayList;
import java.util.Collection;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.NodeAttributes.UnknownState;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.UntypedNodeTraverser;
import com.xmos.xta.engine.references.Reference;

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
	
	public void refreshDisplayedUnknowns() {	
		// Clears the display unknown flags on all nodes..
		for (Node node : getThreads()) {
		    node.traverse(0, new UntypedNodeTraverser() {
		    	public boolean foundNode(Node node, int depth) {
		    		node.getNodeAttributes().setDisplayUnknown(false);
		        	return true;
		    	}
		    });
		}
		// On each of the unknown nodes, mark all of their parents to 'display unknown'.
		for (Node node : getThreads()) {
		    node.traverse(0, new UntypedNodeTraverser() {
		    	public boolean foundNode(Node node, int depth) {
		    		if (node.getNodeAttributes().isUnknown() &&
		    			node.getNodeAttributes().getUnknownState() == UnknownState.UNKNOWN) {
		    			setDisplayUnknownOnParentNodes(node);
		    		}
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
				setUnknownState(enclosingLoopNode, UnknownState.KNOWN);	  				
				found = true;
			}
		}		
		if (found) {
			refreshDisplayedUnknowns();			
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
	    	setUnknownState(node, UnknownState.KNOWN);
	    	found = true;
		}
		if (found) {
			refreshDisplayedUnknowns();			
		}		
		return found;
	}

	public boolean unsetInstructionTimeTaken(Reference reference) {
		boolean found = false;
	    for (Node node : getReferencedNodes(reference)) {
	    	node.unsetInstructionTimeTaken();
	    	setUnknownState(node, UnknownState.UNKNOWN);	    	
	    	found = true;
		}
		if (found) {
			refreshDisplayedUnknowns();			
		}		
		return found;
	}
	
	public boolean setInstructionNumCycles(Reference reference, long numCycles) {
		boolean found = false;
	    for (Node node : getReferencedNodes(reference)) {
	    	node.setInstructionNumCycles(numCycles);
	    	setUnknownState(node, UnknownState.KNOWN);
	    	found = true;
		}
		if (found) {
			refreshDisplayedUnknowns();			
		}		
		return found;
	}
	
	public boolean unsetInstructionNumCycles(Reference reference) {
		boolean found = false;
        for (Node node : getReferencedNodes(reference)) {
        	node.unsetInstructionNumCycles();
        	setUnknownState(node, UnknownState.UNKNOWN);	
	    	found = true;
		}
		if (found) {
			refreshDisplayedUnknowns();			
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
	
	public boolean setExclusion(Reference reference) {
		boolean found = false;
        for (Node node : getReferencedNodes(reference)) {
        	node.setIsExcluded(true);
	    	found = true;
		}
		return found;
	}

	public boolean unsetExclusion(Reference reference) {
		boolean found = false;
        for (Node node : getReferencedNodes(reference)) {
        	node.setIsExcluded(false);
	    	found = true;
		}
		return found;
	}

	public Collection<Reference> getExclusions() {
		return null;
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
		if (node.isLoopNode())
			return node;
				
		Node n = node.getParent();
		while ((n != null) && !n.isLoopNode()) {
			n = node.getParent();
		}
		return n;		
	}

	private void setUnknownState(Node node, UnknownState unknownState) {
		node.getNodeAttributes().setUnknownState(unknownState);					
	}
	
	private void setDisplayUnknownOnParentNodes(Node node) {
		Node parent = node;
		while (parent != null) {
			parent.getNodeAttributes().setDisplayUnknown(true);
			parent = parent.getParent();		    
		}
	}		
}
