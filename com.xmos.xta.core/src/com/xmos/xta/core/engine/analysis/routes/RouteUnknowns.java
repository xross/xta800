package com.xmos.xta.core.engine.analysis.routes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.DefaultNodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.TraverseStatus;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.UntypedNodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown.Source;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown.State;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.UnknownFactory.Type;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.utils.TimeUnit;

public class RouteUnknowns {
	private Route m_route;

	RouteUnknowns(Route route) {
		m_route = route;
	}

	public Collection<Unknown> getUnknowns(final State unknownState) {
	    final Collection<Unknown> unknowns = new ArrayList<Unknown>();
	    m_route.getNode().traverse(new UntypedNodeTraverser() {
	    	public TraverseStatus foundNode(Node node) {
	    		for (Unknown unknown : node.getAttributes().getUnknowns()) {
	    			if (unknown.getState() == unknownState) {
		    			unknowns.add(unknown);
	    			}
	    		}
	        	return TraverseStatus.CONTINUE;
	    	}
	    });
		return unknowns;
	}

	// ===================================================================== //
	// Setting Loop Iterations
	// ===================================================================== //
	public void setLoopIterations(CommandSource src, Engine engine,
			Reference reference, long iterations, boolean sourceLevelIterations, Source source) throws AbortedException {
		for (Node node : m_route.getReferencedNodes(reference)) {
			setLoopIterationsNoCheck(src, engine, node, iterations, sourceLevelIterations, reference, source);
		}
	}

	public void checkSetLoopIterations(Reference reference, long iterations) throws Exception {
		if (iterations < 0)
			throw new Exception("Cannot set a negative iteration count");

		Set<Node> referencedNodes = m_route.getReferencedNodes(reference);
		if (referencedNodes.size() > 0) {
			for (Node node : referencedNodes) {
				checkSetLoopIterations(node);
				checkLoopIterationsValid(node, iterations);
			}
		} else {
			throw new Exception("Reference not found on given route");
		}

	}

	public void setLoopIterations(CommandSource src, Engine engine, Node node, long iterations, Source source) throws Exception {
		if (iterations < 0)
			throw new Exception("Cannot set a negative iteration count");

		checkSetLoopIterations(node);
		checkLoopIterationsValid(node, iterations);
	    setLoopIterationsNoCheck(src, engine, node, iterations, false, null, source);
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
			Node node, long iterations, boolean sourceLevelIterations,
			Reference reference, Source source) {

		invalidateCachedStats();
		Node enclosingLoopNode = node.getEnclosingLoopNode();
	    enclosingLoopNode.getAttributes().setLoopIterations(src, engine, iterations, sourceLevelIterations);
        enclosingLoopNode.getAttributes().addKnown(m_route, Type.LOOP_ITERATIONS, source, new Reference[] {reference});
   		clearPathIterations(enclosingLoopNode);
		refreshDisplayedUnknowns();
	}

	// ===================================================================== //
	// Setting Loop Path Iterations
	// ===================================================================== //
	public void setLoopPathIterations(Reference reference, long iterations, Source source) throws AbortedException {
		for (Node node : m_route.getReferencedNodes(reference)) {
			setLoopPathIterationsNoCheck(node, iterations, reference, source);
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

	public void setLoopPathIterations(Node node, long iterations, Source source) throws Exception {
		if (iterations < 0)
			throw new Exception("Cannot set a negative iteration count");

		checkSetLoopPathIterations(node);
		checkLoopPathIterationsValid(node, iterations);
		setLoopPathIterationsNoCheck(node, iterations, null, source);
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

	private void setLoopPathIterationsNoCheck(Node node, long iterations, Reference reference, Source source) {
		invalidateCachedStats();
		Node condNode = node.getEnclosingCondNode();
		Node optionNode = node.getEnclosingOptionNode();
	    condNode.getAttributes().setLoopPathIterations(optionNode, iterations);
	    optionNode.getAttributes().addKnown(m_route, Type.LOOP_PATH_ITERATIONS, source, new Reference[] {reference});
		refreshDisplayedUnknowns();
	}

	// ===================================================================== //
	// Setting Loop Scope
	// ===================================================================== //
	public void setLoopScope(Reference reference, boolean isAbsolute, Source source) throws AbortedException {

		for (Node node : m_route.getReferencedNodes(reference)) {
			setLoopScopeNoCheck(node, isAbsolute, reference, source);
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

	public void setLoopScope(Node node, boolean isAbsolute, Source source) throws Exception {
		checkSetLoopScope(node);
		setLoopScopeNoCheck(node, isAbsolute, null, source);
	}

	public void checkSetLoopScope(Node node) throws Exception {
		if (node.getEnclosingLoopNode() == null)
			throw new Exception("Must be within a loop to be able to set loop scope");
	}

	public boolean getSetLoopScope(Node node) {
		Node enclosingLoopNode = node.getEnclosingLoopNode();
		return enclosingLoopNode.getAttributes().getIsAbsolute();
	}

	private void setLoopScopeNoCheck(Node node, boolean isAbsolute, Reference reference, Source source) {
		invalidateCachedStats();
    	Node enclosingLoopNode = node.getEnclosingLoopNode();
		enclosingLoopNode.getAttributes().setIsAbsolute(isAbsolute);
		enclosingLoopNode.getAttributes().addKnown(m_route, Type.LOOP_SCOPE, source, new Reference[] {reference});
	}

	// ===================================================================== //
	// Fixed Time Methods
	// ===================================================================== //
	public boolean setFixedTime(Reference reference, TimeUnit timeUnit, Type type, Source source) throws AbortedException {
		boolean found = false;
	    for (Node node : m_route.getReferencedNodes(reference)) {
	    	setFixedTime(node, timeUnit, reference, type, source);
	    	found = true;
		}
		return found;
	}

	public void setFixedTime(Node node, TimeUnit timeUnit, Type type, Source source) {
		setFixedTime(node, timeUnit, null, type, source);
	}

	public void setFixedTime(Node node, TimeUnit timeUnit, Reference reference, Type type, Source source) {
		timeUnit.setCoreConfig(m_route.getRouteCoreContainer().getCoreConfig());
		invalidateCachedStats();
    	node.getAttributes().setFixedTime(timeUnit);
	    node.getAttributes().addKnown(m_route, type, source, new Reference[] {reference});
		refreshDisplayedUnknowns();
	}

	public TimeUnit getFixedTime(Node node) {
		return node.getAttributes().getFixedTime();
	}

	public boolean canInstructionPause(Reference reference) throws AbortedException {
		boolean canPause = false;
		
        for (Node node : m_route.getReferencedNodes(reference)) {
        	if (((InstructionNode)node).getXtaInstruction().canPause()) {
        		canPause = true;
        	}
		}
		return canPause;
	}

	void invalidateCachedStats() {
		m_route.getNode().traverse(new UntypedNodeTraverser() {
	    	public TraverseStatus foundNode(Node node) {
	    		node.getAttributes().invalidateCachedStats();
	        	return TraverseStatus.CONTINUE;
	    	}
	    });
	}

	// ===================================================================== //
	// Helper Methods
	// ===================================================================== //
	public void refreshDisplayedUnknowns() {
		refreshDisplayedUnknowns(m_route.getNode());
	}

	private void refreshDisplayedUnknowns(Node node) {
		// Clears the display unknown flags on all nodes..
	    node.traverse(new UntypedNodeTraverser() {
	    	public TraverseStatus foundNode(Node node) {
	    		node.getAttributes().setDisplayAsUnknown(false);
	        	return TraverseStatus.CONTINUE;
	    	}
	    });

		// On each of the unknown nodes, mark all of their parents to 'display unknown'.
	    node.traverse(new UntypedNodeTraverser() {
	    	public TraverseStatus foundNode(Node node) {
	    		if (node.getAttributes().hasUnknowns()) {
	    			setDisplayAsUnknownOnParentNodes(node);
	    		}
	        	return TraverseStatus.CONTINUE;
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
	    node.traverse( new DefaultNodeTraverser() {
	    	public TraverseStatus foundConditionalNode(Node node) {
	    		node.getAttributes().resetPathIterations();
	        	return TraverseStatus.CONTINUE;
	    	}
	    });
	}
}
