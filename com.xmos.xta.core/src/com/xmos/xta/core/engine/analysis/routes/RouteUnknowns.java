package com.xmos.xta.core.engine.analysis.routes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.Unknown;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.Unknown.State;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.Unknown.Type;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.DefaultNodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.UntypedNodeTraverser;
import com.xmos.xta.core.engine.references.Reference;

public class RouteUnknowns {
	private Route m_route;

	RouteUnknowns(Route route) {
		m_route = route;
	}

	public Collection<Unknown> getUnknowns(final State unknownState) {
	    final Collection<Unknown> unknowns = new ArrayList<Unknown>();
	    m_route.getNode().traverse(new Stack<Node>(), new UntypedNodeTraverser() {
	    	public boolean foundNode(Node node, Stack<Node> ancestors) {
	    		for (Unknown unknown : node.getAttributes().getUnknowns()) {
	    			if (unknown.getState() == unknownState) {
		    			unknowns.add(unknown);
	    			}
	    		}
	        	return true;
	    	}
	    });
		return unknowns;
	}

	// ===================================================================== //
	// Setting Loop Iterations
	// ===================================================================== //
	public void setLoopIterations(CommandSource src, Engine engine,
			Reference reference, long iterations, boolean sourceLevelIterations) {
		
		for (Node node : m_route.getReferencedNodes(reference)) {
			setLoopIterationsNoCheck(src, engine, node, iterations, sourceLevelIterations, reference);
		}			
	}

	public void checkSetLoopIterations(Reference reference, long iterations) throws Exception {
		if (iterations < 0)
			throw new Exception("Cannot set a negative iteration count");		

		Collection<Node> referencedNodes = m_route.getReferencedNodes(reference);
		if (referencedNodes.size() > 0) {
			for (Node node : referencedNodes) {
				checkSetLoopIterations(node); 
				checkLoopIterationsValid(node, iterations);
			}
		} else {
			throw new Exception("Reference not found on given route");
		}
	}

	public void setLoopIterations(CommandSource src, Engine engine, Node node, long iterations) throws Exception {
		if (iterations < 0)
			throw new Exception("Cannot set a negative iteration count");

		checkSetLoopIterations(node);
		checkLoopIterationsValid(node, iterations);
	    setLoopIterationsNoCheck(src, engine, node, iterations, false, null);
	}
	
	public void checkSetLoopIterations(Node node) throws Exception {
		if (node.getEnclosingInfiniteLoopNode() != null)
			throw new Exception("Cannot set loop iterations on an infintie loop");
		
		if (node.getEnclosingLoopNode() == null)
			throw new Exception("Must be within a loop to be able to set loop iterations");
	}

	public long getSetLoopIterations(Node node) {
		Node enclosingLoopNode = node.getEnclosingLoopNode();
		if (enclosingLoopNode == null) {
			return 1;
		}
		return enclosingLoopNode.getAttributes().getLoopIterations();
	}		

	private void checkLoopIterationsValid(Node node, long iterations) throws Exception {		
		Node loopNode = node.getEnclosingLoopNode();
		loopNode.getAttributes().loopIterationsValid(iterations);
	}
	
	private void setLoopIterationsNoCheck(CommandSource src, Engine engine,
			Node node, long iterations, boolean sourceLevelIterations, Reference reference) {

		invalidateCachedStats();
		Node enclosingLoopNode = node.getEnclosingLoopNode();		
	    enclosingLoopNode.getAttributes().setLoopIterations(src, engine, iterations, sourceLevelIterations);	    
	    enclosingLoopNode.getAttributes().addKnown(Type.LOOP_ITERATIONS, reference);
   		clearPathIterations(enclosingLoopNode);
		refreshDisplayedUnknowns();
	}

	// ===================================================================== //
	// Setting Loop Path Iterations
	// ===================================================================== //
	public void setLoopPathIterations(Reference reference, long iterations) {
		for (Node node : m_route.getReferencedNodes(reference)) {
			setLoopPathIterationsNoCheck(node, iterations, reference);
		}		
	}
	
	public void checkSetLoopPathIterations(Reference reference, long iterations) throws Exception {
		if (iterations < 0)
			throw new Exception("Cannot set a negative iteration count");		

		Collection<Node> referencedNodes = m_route.getReferencedNodes(reference);
		if (referencedNodes.size() > 0) {
			for (Node node : referencedNodes) {
				checkSetLoopPathIterations(node); 
				checkLoopPathIterationsValid(node, iterations);
			}		
		} else {
			throw new Exception("Reference not found on given route");
		}
	}
	
	public void setLoopPathIterations(Node node, long iterations) throws Exception {
		if (iterations < 0)
			throw new Exception("Cannot set a negative iteration count");
		
		checkSetLoopPathIterations(node);
		checkLoopPathIterationsValid(node, iterations);
		setLoopPathIterationsNoCheck(node, iterations, null);
	}

	public void checkSetLoopPathIterations(Node node) throws Exception {
		if (node.getEnclosingInfiniteLoopNode() != null)
			throw new Exception("Cannot set loop iterations on an infintie loop");

		if (node.getEnclosingLoopNode() == null)
			throw new Exception("Must be within a loop to be able to set loop path iterations");
		
		if (node.getEnclosingCondNode() == null)
			throw new Exception("Must be within a condition to be able to set loop path iterations");

		if (node.getEnclosingOptionNode() == null)
			throw new Exception("Must be within a condition to be able to set loop path iterations");
	}

	public long getSetLoopPathIterations(Node node) {
		Node condNode = node.getEnclosingCondNode();
		Node optionNode = node.getEnclosingOptionNode();
	    long iterations = condNode.getAttributes().getSetLoopPathIterations(optionNode);
	    return iterations;
	}

	private void checkLoopPathIterationsValid(Node node, long iterations) throws Exception {
		Node condNode = node.getEnclosingCondNode();
		Node optionNode = node.getEnclosingOptionNode();
		condNode.getAttributes().loopPathIterationsValid(optionNode, iterations);
	}

	private void setLoopPathIterationsNoCheck(Node node, long iterations, Reference reference) {
		invalidateCachedStats();
		Node condNode = node.getEnclosingCondNode();
		Node optionNode = node.getEnclosingOptionNode();
	    condNode.getAttributes().setLoopPathIterations(optionNode, iterations);	    
	    optionNode.getAttributes().addKnown(Type.LOOP_PATH_ITERATIONS, reference);
		refreshDisplayedUnknowns();		
	}
	
	// ===================================================================== //
	// Setting Loop Scope
	// ===================================================================== //
	public void setLoopScope(Reference reference, boolean isAbsolute) {
		for (Node node : m_route.getReferencedNodes(reference)) {
			setLoopScopeNoCheck(node, isAbsolute, reference);
		}		
	}

	public void checkSetLoopScope(Reference reference, boolean isAbsolute) throws Exception {
		Collection<Node> referencedNodes = m_route.getReferencedNodes(reference);
		if (referencedNodes.size() > 0) {
			for (Node node : referencedNodes) {
				checkSetLoopScope(node);
			}		
		} else {
			throw new Exception("Reference not found on given route");
		}
	}
	
	public void setLoopScope(Node node, boolean isAbsolute) throws Exception {
		checkSetLoopScope(node);
		setLoopScopeNoCheck(node, isAbsolute, null);
	}	

	public void checkSetLoopScope(Node node) throws Exception {
		if (node.getEnclosingLoopNode() == null)
			throw new Exception("Must be within a loop to be able to set loop scope");
	}
	
	public boolean getSetLoopScope(Node node) {
		Node enclosingLoopNode = node.getEnclosingLoopNode();
		return enclosingLoopNode.getAttributes().getIsAbsolute();
	}
	
	private void setLoopScopeNoCheck(Node node, boolean isAbsolute, Reference reference) {
		invalidateCachedStats();		
    	Node enclosingLoopNode = node.getEnclosingLoopNode();
		enclosingLoopNode.getAttributes().setIsAbsolute(isAbsolute);
		enclosingLoopNode.getAttributes().addKnown(Type.LOOP_SCOPE, reference);
	}

	// ===================================================================== //
	// Setting Instruction Time (Cycles)
	// ===================================================================== //	
	public boolean setInstructionCycles(Reference reference, long numCycles) {
		boolean found = false;
	    for (Node node : m_route.getReferencedNodes(reference)) {
	    	setInstructionCycles(node, numCycles, reference);
	    	found = true;
		}
		return found;
	}

	public void setInstructionCycles(Node node, long numCycles) {
		setInstructionCycles(node, numCycles, null);
	}
		
	public long getInstructionCycles(Node node) {
		return node.getAttributes().getInstructionCycles();
	}	
	
	private void setInstructionCycles(Node node, long numCycles, Reference reference) {
		invalidateCachedStats();
    	node.getAttributes().setInstructionCycles(numCycles);
		node.getAttributes().addKnown(Type.INSTRUCTION_TIME, reference);
		refreshDisplayedUnknowns();
	}
	
	// ===================================================================== //
	// Setting Instruction Time (Ns)
	// ===================================================================== //
	public boolean setInstructionNs(Reference reference, double ns) {
		boolean found = false;
	    for (Node node : m_route.getReferencedNodes(reference)) {
	    	setInstructionNs(node, ns, reference);
	    	found = true;
		}
		return found;
	}
	
	public void setInstructionNs(Node node, double ns) {
		 setInstructionNs(node, ns, null);
    }

	public double getInstructionNs(Node node) {
		return node.getAttributes().getInstructionNs();
	}	
			
	private void setInstructionNs(Node node, double ns, Reference reference) {
		invalidateCachedStats();
    	node.getAttributes().setInstructionNs(ns);
		node.getAttributes().addKnown(Type.INSTRUCTION_TIME, reference);
		refreshDisplayedUnknowns();
	}
	
	// ===================================================================== //
	// Setting Instruction Time (Mhz)
	// ===================================================================== //		
	public boolean setInstructionMhz(Reference reference, double mhz) {
		boolean found = false;
	    for (Node node : m_route.getReferencedNodes(reference)) {
	    	setInstructionMhz(node, mhz, reference);
	    	found = true;
		}
		return found;
	}
	
	public void setInstructionMhz(Node node, double mhz) {
		setInstructionMhz(node, mhz, null);
	}

	public double getInstructionMhz(Node node) {
		return node.getAttributes().getInstructionMhz();
	}

	private void setInstructionMhz(Node node, double mhz, Reference reference) {
		invalidateCachedStats();
    	node.getAttributes().setInstructionMhz(mhz);
		node.getAttributes().addKnown(Type.INSTRUCTION_TIME, reference);
		refreshDisplayedUnknowns();	
	}
	
	// ===================================================================== //
	// Unsetting Instruction Time
	// ===================================================================== //		
	public boolean unsetInstructionTime(Reference reference) {
		boolean found = false;
	    for (Node node : m_route.getReferencedNodes(reference)) {
	    	unsetInstructionTime(node);
	    	found = true;
		}
		return found;
	}

	public void unsetInstructionTime(Node node) {
		invalidateCachedStats();
    	node.getAttributes().unsetInstructionTime();
		node.getAttributes().addUnknown(Type.INSTRUCTION_TIME);
		refreshDisplayedUnknowns();
	}
	
	public boolean canInstructionPause(Reference reference) {
		boolean canPause = true;
        for (Node node : m_route.getReferencedNodes(reference)) {
        	if (!((InstructionNode)node).getXtaInstruction().canPause()) {
        		canPause = false;
        	}
		}
		return canPause;
	}
	
	// ===================================================================== //
	// Helper Methods
	// ===================================================================== //	
	private void refreshDisplayedUnknowns() {
		refreshDisplayedUnknowns(m_route.getNode());
	}
	
	private void refreshDisplayedUnknowns(Node node) {	
		// Clears the display unknown flags on all nodes..
	    node.traverse(new Stack<Node>(), new UntypedNodeTraverser() {
	    	public boolean foundNode(Node node, Stack<Node> ancestors) {
	    		node.getAttributes().setDisplayAsUnknown(false);
	        	return true;
	    	}
	    });

		// On each of the unknown nodes, mark all of their parents to 'display unknown'.
	    node.traverse(new Stack<Node>(), new UntypedNodeTraverser() {
	    	public boolean foundNode(Node node, Stack<Node> ancestors) {
	    		if (node.getAttributes().hasUnknowns()) {
	    			setDisplayAsUnknownOnParentNodes(node);
	    		}
	        	return true;
	    	}
	    });	   
	}	
	
	private void setDisplayAsUnknownOnParentNodes(Node node) {
		Node parent = node;
		while (parent != null) {
			parent.getAttributes().setDisplayAsUnknown(true);
			parent = parent.getParent();		    
		}
	}
	
	private void clearPathIterations(Node node) {
	    node.traverse(new Stack<Node>(), new DefaultNodeTraverser() {
	    	public boolean foundConditionalNode(Node node, Stack<Node> ancestors) {
	    		node.getAttributes().resetPathIterations();
	        	return true;
	    	}
	    });		
	}
	
	private void invalidateCachedStats() {
		m_route.getNode().traverse(new Stack<Node>(), new UntypedNodeTraverser() {
	    	public boolean foundNode(Node node, Stack<Node> ancestors) {
	    		node.getAttributes().invalidateCachedStats();
	        	return true;
	    	}
	    });		
	}
}
