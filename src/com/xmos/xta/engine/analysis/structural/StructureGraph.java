package com.xmos.xta.engine.analysis.structural;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.SectionNode;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes.UnknownState;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.UntypedNodeTraverser;
import com.xmos.xta.engine.references.Reference;

public class StructureGraph {
	private Collection<Node> m_threads;
	private Collection<Reference> m_exclusions;
	
	public StructureGraph(Collection<Node> threads) {
		super();
		assert(threads.size() > 0);
		m_threads = threads;
		m_exclusions = new ArrayList<Reference>();
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
			Node exclusionRootNode = getExclusionRoot(node);			
			if (checkIterationsPreExclusion(node)) {
    			excludeNodes(exclusionRootNode, true);
    			refreshIterationsPostExclusion(exclusionRootNode);
	    		addToExclusionList(reference);
	        	found = true;
			}
		}
		return found;
	}

	public boolean unsetExclusion(Reference reference) {
		boolean found = false;
        for (Node node : getReferencedNodes(reference)) {
			Node exclusionRootNode = getExclusionRoot(node);
			excludeNodes(exclusionRootNode, false);
			refreshIterationsPostExclusion(exclusionRootNode);
			removeFromExclusionList(reference);
	    	found = true;
		}
		return found;
	}

	public Collection<Reference> getExclusions() {
		return m_exclusions;
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
	
	private Node getExclusionRoot(Node node) {
		Node currentNode = node;
		while (!currentNode.getParent().isSectionNode() || 
				noOptionsLeft(currentNode.getParent())) {
			
			currentNode = currentNode.getParent();
			if (currentNode.getParent() == null)
				break;
		}
		return currentNode;
	}
	
	private boolean noOptionsLeft(Node currentNode) {
		SectionNode section = (SectionNode)currentNode;
						
		int numOptionsLeft = 0;
		for (Node option : section.getOptions()) {
			if (!option.isExcluded())
			    ++numOptionsLeft;
		}
		
		if (section.isOptional()) {
			++numOptionsLeft;
		}
		return (numOptionsLeft < 2);
	}	
	
	private void excludeNodes(Node node, final boolean value) {
		node.setIsExcluded(value);
		node.traverse(0, new UntypedNodeTraverser() {
			public boolean foundNode(Node node, int depth) {
				node.setIsExcluded(value);
				return true;
			}
		});
	}
	
	private void addToExclusionList(Reference reference) {
		m_exclusions.add(reference);		
	}

	private void removeFromExclusionList(Reference reference) {
		Iterator<Reference> iter = m_exclusions.iterator();
		while (iter.hasNext()) {
			Reference ref = iter.next();
			if (ref.equals(reference)) {
				iter.remove();
			}
		}
	}	
	
	private boolean checkIterationsPreExclusion(Node node) {
		// This checks that we are not setting an exclusion on
		// a node where we shouldn't be...
		Node currentNode = node;
		while (!currentNode.getParent().isSectionNode() || 
				noOptionsLeft(currentNode.getParent())) {
			
			currentNode = currentNode.getParent();			
			if (currentNode.getParent() == null)
				return true;
		}
		
		if (currentNode.getParent().isSectionNode()) {
			Node sectionNode = currentNode.getParent();
			Map<Node, Boolean> setOptionsMap = sectionNode.getAttributes().getSetOptions();
			
			// The iterations have been explicitly set by the user,
			// so this is not allowed..
			if (setOptionsMap.get(currentNode)) 
				return false;
			
			// If we were to exclude this option, and this option had some iterations
			// that contribute toward the total, then is there another option available
			// that can take on the ones that will be excluded?
			List<Node> setOptions = sectionNode.getAllSetOptions();
			List<Node> unsetOptions = sectionNode.getAllUnsetOptions();
			if ((setOptions.size() > 1) && (unsetOptions.size() < 1))
				return false;
		}		
		return true;
	}
	
	private void refreshIterationsPostExclusion(Node exclusionRootNode) {
		// Once an exclusion has been done, then this makes sure
		// that any (non explicit) best/worst iterations that were assigned
		// to a excluded node are reassigned to the next best/worst case.		
		exclusionRootNode.traverse(0, new UntypedNodeTraverser() {
			public boolean foundNode(Node node, int depth) {
				if (node.isExcluded()) {
					refreshIterationsPostExclusion(node, Case.WORST);
					refreshIterationsPostExclusion(node, Case.BEST);					
				}								
				return true;
			}
	    });		
	}
	
	private void refreshIterationsPostExclusion(Node node, Case c) {
		long numIterations = node.getAttributes().getNumIterations(c);
   		if (numIterations > 0) {
   			// Makes sure that the excluded nodes iterations are set to zero...
   			node.getAttributes().setLoopIterations(c, 0);			   			
   			
   			// If this is a section, then reassign the excluded iterations 
   			// to the nest best/worst case option..
   			Node sectionNode = node.getEnclosingSectionNode();
   			if (sectionNode != null) {
	   			Node worstCaseOption = sectionNode.getOption(c, true);
	   			if (worstCaseOption != null) {
	   				worstCaseOption.getAttributes().setLoopIterations(c, numIterations);
	   			}			   				
   			}			   			
   		}		
	}	
}
