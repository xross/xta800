package com.xmos.xta.engine.analysis.structural;

import java.util.ArrayList;
import java.util.Collection;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes.UnknownState;
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
		    		node.getAttributes().setDisplayUnknown(false);
		        	return true;
		    	}
		    });
		}
		// On each of the unknown nodes, mark all of their parents to 'display unknown'.
		for (Node node : getThreads()) {
		    node.traverse(0, new UntypedNodeTraverser() {
		    	public boolean foundNode(Node node, int depth) {
		    		if (node.getAttributes().isUnknown() &&
		    			node.getAttributes().getUnknownState() == UnknownState.UNKNOWN) {
		    			setDisplayUnknownOnParentNodes(node);
		    		}
		        	return true;
		    	}
		    });	   
		}				
	}	
	
	public boolean setLoopIterations(Reference reference, long iterations) {
		if (iterations < 0)
			return false;
		
		boolean found = false;
		for (Node node : getReferencedNodes(reference)) {						
			Node enclosingLoopNode = node.getEnclosingLoopNode();
			if (enclosingLoopNode != null) {				
				enclosingLoopNode.getAttributes().setLoopIterations(iterations);
				enclosingLoopNode.getAttributes().setUnknownState(UnknownState.KNOWN);	
				found = true;
			}
		}		
		if (found) {
			refreshDisplayedUnknowns();			
		}		
		return found;
	}

	public boolean setLoopPathIterations(Reference reference, long iterations) {
		if (iterations < 0)
			return false;

		boolean found = false;
		for (Node node : getReferencedNodes(reference)) {						
			Node loopNode = node.getEnclosingLoopNode();
			if (loopNode == null)
				continue;
			
			Node sectionNode = node.getEnclosingSectionNode();
			if (sectionNode == null)
				continue;
			
			Node optionNode = node.getEnclosingOptionNode();
			if (optionNode == null)
				continue;
			
			if (!sectionNode.getAttributes().setLoopPathIterations(loopNode, optionNode, iterations))
				continue;
			
			found = true;
		}		
		if (found) {
			refreshDisplayedUnknowns();			
		}		
		return found;		
	}
	
	public boolean setLoopScope(Reference reference, boolean isAbsolute) {
		boolean found = false;
	    for (Node node : getReferencedNodes(reference)) {
			Node enclosingLoopNode = node.getEnclosingLoopNode();
			if (enclosingLoopNode != null) {				
				enclosingLoopNode.getAttributes().setIsAbsolute(isAbsolute);
				found = true;
			}
		}		
		return found;
	}
	
	public boolean setInstructionTimeTaken(Reference reference, double timeTaken) {
		boolean found = false;
	    for (Node node : getReferencedNodes(reference)) {
	    	node.getAttributes().setInstructionTimeTaken(timeTaken);
			node.getAttributes().setUnknownState(UnknownState.KNOWN);	
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
	    	node.getAttributes().unsetInstructionTimeTaken();
			node.getAttributes().setUnknownState(UnknownState.UNKNOWN);	
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
	    	node.getAttributes().setInstructionNumCycles(numCycles);
			node.getAttributes().setUnknownState(UnknownState.KNOWN);	
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
        	node.getAttributes().unsetInstructionNumCycles();
			node.getAttributes().setUnknownState(UnknownState.UNKNOWN);	
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
		
	private void setDisplayUnknownOnParentNodes(Node node) {
		Node parent = node;
		while (parent != null) {
			parent.getAttributes().setDisplayUnknown(true);
			parent = parent.getParent();		    
		}
	}		
}
