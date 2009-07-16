package com.xmos.xta.engine.analysis.routes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes.UnknownState;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.DefaultNodeTraverser;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.UntypedNodeTraverser;
import com.xmos.xta.engine.instructions.XtaInstruction;
import com.xmos.xta.engine.references.Reference;

public class RouteUnknowns {
	private Route m_route;

	RouteUnknowns(Route route) {
		m_route = route;
	}

	public Collection<Node> getUnknowns(final UnknownState unknownState) {
	    final Collection<Node> unknowns = new ArrayList<Node>();
	    m_route.getNode().traverse(0, new UntypedNodeTraverser() {
	    	public boolean foundNode(Node node, int depth) {
	    		NodeAttributes nodeAttributes = node.getAttributes();		    		
	    		if (nodeAttributes.isUnknown() &&
	    			(nodeAttributes.getUnknownState() == unknownState)) {
	    			unknowns.add(node);
	    		}		    			
	        	return true;
	    	}
	    });
		return unknowns;
	}	
	
	// ===================================================================== //
	// Setting Loop Iteration
	// ===================================================================== //
	public boolean setLoopIterations(Reference reference, long iterations) {
		if (iterations < 0)
			return false;		

		for (Node node : m_route.getReferencedNodes(reference)) {
			if ((canSetLoopIterations(node) == false) || 
				(loopIterationsValid(node, iterations) == false))
				return false;
		}
		for (Node node : m_route.getReferencedNodes(reference)) {
			setLoopIterationsNoCheck(node, iterations);
		}			
		return true;
	}

	public boolean setLoopIterations(Node node, long iterations) {
		if (iterations < 0)
			return false;		

		if (canSetLoopIterations(node) && loopIterationsValid(node, iterations)) {
			setLoopIterationsNoCheck(node, iterations);
			return true;
		}
		return false;
	}
	
	public boolean canSetLoopIterations(Node node) {
		if (node.getEnclosingLoopNode() == null)
			return false;
		
		return true;
	}

	public long getSetLoopIterations(Node node) {
		Node enclosingLoopNode = node.getEnclosingLoopNode();
		if (enclosingLoopNode == null) {
			return 1;
		}
		return enclosingLoopNode.getAttributes().getLoopIterations();
	}		

	private boolean loopIterationsValid(Node node, long iterations) {		
		Node loopNode = node.getEnclosingLoopNode();
		return loopNode.getAttributes().loopIterationsValid(iterations);
	}
	
	private void setLoopIterationsNoCheck(Node node, long iterations) {
		Node enclosingLoopNode = node.getEnclosingLoopNode();		
	    enclosingLoopNode.getAttributes().setLoopIterations(iterations);
		enclosingLoopNode.getAttributes().setUnknownState(UnknownState.KNOWN);	
   		clearPathIterations(enclosingLoopNode);
		refreshDisplayedUnknowns();
		invalidateCachedStats();				
	}

	// ===================================================================== //
	// Setting Loop Path Iteration
	// ===================================================================== //
	public boolean setLoopPathIterations(Reference reference, long iterations) {
		if (iterations < 0)
			return false;		
		
		for (Node node : m_route.getReferencedNodes(reference)) {
			if ((canSetLoopPathIterations(node) == false) || 
				(loopPathIterationsValid(node, iterations) == false))
				return false;
		}		
		for (Node node : m_route.getReferencedNodes(reference)) {
			setLoopPathIterationsNoCheck(node, iterations);
		}		
		return true;
	}
	
	public boolean setLoopPathIterations(Node node, long iterations) {
		if (iterations < 0)
			return false;		
		
		if (canSetLoopPathIterations(node) && loopPathIterationsValid(node, iterations)) {
			setLoopPathIterationsNoCheck(node, iterations);
			return true;
		}
		return false;
	}

	public boolean canSetLoopPathIterations(Node node) {
		Node loopNode = node.getEnclosingLoopNode();
		if (loopNode == null)
			return false;
		
		Node sectionNode = node.getEnclosingSectionNode();
		if (sectionNode == null)
			return false;
		
		Node optionNode = node.getEnclosingOptionNode();
		if (optionNode == null)
			return false;

		return true;
	}

	public long getSetLoopPathIterations(Node node) {
		assert(canSetLoopPathIterations(node));
		Node sectionNode = node.getEnclosingSectionNode();
		Node optionNode = node.getEnclosingOptionNode();
	    long iterations = sectionNode.getAttributes().getSetLoopPathIterations(optionNode);
	    return iterations;
	}

	private boolean loopPathIterationsValid(Node node, long iterations) {
		Node loopNode = node.getEnclosingLoopNode();
		Node sectionNode = node.getEnclosingSectionNode();
		Node optionNode = node.getEnclosingOptionNode();
		return sectionNode.getAttributes().loopPathIterationsValid(loopNode, optionNode, iterations);
	}

	private void setLoopPathIterationsNoCheck(Node node, long iterations) {
		Node loopNode = node.getEnclosingLoopNode();
		Node sectionNode = node.getEnclosingSectionNode();
		Node optionNode = node.getEnclosingOptionNode();
	    sectionNode.getAttributes().setLoopPathIterations(loopNode, optionNode, iterations);
		refreshDisplayedUnknowns();		
		invalidateCachedStats();
	}
	
	// ===================================================================== //
	// Setting Loop Scope
	// ===================================================================== //
	public boolean setLoopScope(Reference reference, boolean isAbsolute) {
		for (Node node : m_route.getReferencedNodes(reference)) {
			if (canSetLoopScope(node) == false)
				return false;
		}		
		for (Node node : m_route.getReferencedNodes(reference)) {
			setLoopScopeNoCheck(node, isAbsolute);
		}		
		return true;
	}

	public boolean setLoopScope(Node node, boolean isAbsolute) {
		if (canSetLoopScope(node)) {
			setLoopScopeNoCheck(node, isAbsolute);
			return true;
		}
		return false;
	}	

	public boolean canSetLoopScope(Node node) {
		return node.getEnclosingLoopNode() != null;
	}
	
	private void setLoopScopeNoCheck(Node node, boolean isAbsolute) {
    	Node enclosingLoopNode = node.getEnclosingLoopNode();
		enclosingLoopNode.getAttributes().setIsAbsolute(isAbsolute);
		invalidateCachedStats();		
	}
	
	// ===================================================================== //
	// Setting Instruction Time
	// ===================================================================== //
	public boolean setInstructionTimeTaken(Reference reference, double timeTaken) {
		boolean found = false;
	    for (Node node : m_route.getReferencedNodes(reference)) {
	    	setInstructionTimeTaken(node, timeTaken);
	    	found = true;
		}
		return found;
	}
	
	public void setInstructionTimeTaken(Node node, double timeTaken) {
    	node.getAttributes().setInstructionTimeTaken(timeTaken);
		node.getAttributes().setUnknownState(UnknownState.KNOWN);	
		refreshDisplayedUnknowns();	
		invalidateCachedStats();
	}

	public boolean unsetInstructionTimeTaken(Reference reference) {
		boolean found = false;
	    for (Node node : m_route.getReferencedNodes(reference)) {
	    	unsetInstructionTimeTaken(node);
	    	found = true;
		}
		return found;
	}

	public void unsetInstructionTimeTaken(Node node) {
    	node.getAttributes().unsetInstructionTimeTaken();
		node.getAttributes().setUnknownState(UnknownState.UNKNOWN);	
		refreshDisplayedUnknowns();
		invalidateCachedStats();
	}
	
	public double getInstructionTimeTaken(Node node) {
		return node.getAttributes().getInstructionTimeTaken();
	}	

	public boolean setInstructionNumCycles(Reference reference, long numCycles) {
		boolean found = false;
	    for (Node node : m_route.getReferencedNodes(reference)) {
	    	setInstructionNumCycles(node, numCycles);
	    	found = true;
		}
		return found;
	}

	public void setInstructionNumCycles(Node node, long numCycles) {
    	node.getAttributes().setInstructionNumCycles(numCycles);
		node.getAttributes().setUnknownState(UnknownState.KNOWN);	
		refreshDisplayedUnknowns();		
		invalidateCachedStats();
	}
	
	public boolean unsetInstructionNumCycles(Reference reference) {
		boolean found = false;
        for (Node node : m_route.getReferencedNodes(reference)) {
        	unsetInstructionNumCycles(node);
	    	found = true;
		}
		return found;
	}

	public void unsetInstructionNumCycles(Node node) {
    	node.getAttributes().unsetInstructionNumCycles();
		node.getAttributes().setUnknownState(UnknownState.UNKNOWN);			
		refreshDisplayedUnknowns();			
		invalidateCachedStats();
	}
		
	public long getInstructionNumCycles(Node node) {
		return node.getAttributes().getInstructionNumCycles();
	}	
	
	public boolean canInstructionPause(Reference reference) {
		boolean canPause = true;
        for (Node node : m_route.getReferencedNodes(reference)) {
        	LinkedList<XtaInstruction> instructions = node.getInstructions();
        	assert(instructions.size() == 1);
        	if (!instructions.getFirst().canPause()) {
        		canPause = false;
        	}
		}
		return canPause;
	}

	private void refreshDisplayedUnknowns() {
		refreshDisplayedUnknowns(m_route.getNode());
	}
	
	private void refreshDisplayedUnknowns(Node node) {	
		// Clears the display unknown flags on all nodes..
	    node.traverse(0, new UntypedNodeTraverser() {
	    	public boolean foundNode(Node node, int depth) {
	    		node.getAttributes().setDisplayUnknown(false);
	        	return true;
	    	}
	    });

		// On each of the unknown nodes, mark all of their parents to 'display unknown'.
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
	
	private void setDisplayUnknownOnParentNodes(Node node) {
		Node parent = node;
		while (parent != null) {
			parent.getAttributes().setDisplayUnknown(true);
			parent = parent.getParent();		    
		}
	}
	
	private void clearPathIterations(Node node) {
	    node.traverse(0, new DefaultNodeTraverser() {
	    	public boolean foundSectionNode(Node node, int depth) {
	    		node.getAttributes().resetPathIterations();
	        	return true;
	    	}
	    });		
	}
	
	private void invalidateCachedStats() {
		m_route.getNode().traverse(0, new UntypedNodeTraverser() {
	    	public boolean foundNode(Node node, int depth) {
	    		node.getAttributes().invalidateCachedStats();
	        	return true;
	    	}
	    });		
	}
}
