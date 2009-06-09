package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import com.xmos.xta.engine.instructions.XtaInstruction;
import com.xmos.xta.engine.references.types.ReferenceType;

public class SelfLoopNode extends Node{
	private Node m_loop;

	public SelfLoopNode(SelfLoopNode loop) {
		super();
		m_loop = loop.m_loop;
	}

	public SelfLoopNode() {
		super();
		m_loop = null;
	}

	public SelfLoopNode(Node base) {
		super();
		m_loop = base;
	}

	public SelfLoopNode shallowCopy() {
		return new SelfLoopNode(this);
	}
	
	public Node deepCopy(Map<Integer, FunctionNode> startPcs) {
		SelfLoopNode ln = new SelfLoopNode();
		ln.m_loop = m_loop.deepCopy(startPcs);
		return ln;
	}
	
	public LinkedList<XtaInstruction> getInstructions() {
	LinkedList<XtaInstruction> insts = new LinkedList<XtaInstruction>(m_loop.getInstructions());

		return insts;
	}

	public Set<Integer> getPCs() {
		Set<Integer> pcs = new HashSet<Integer>(m_loop.getPCs());
		return pcs;
	}

	public Collection<Node> getContainedNodes() {
		Collection<Node> containedNodes = new HashSet<Node>();
		containedNodes.addAll(m_loop.getContainedNodes());
		return containedNodes;
	}

	public String getFunctionName() {
		return "";
	}

	public int getStartPc() {
		return m_loop.getStartPc();
	}

	public void traverse(int depth, NodeTraverser nodeTraverser) {
		if (!nodeTraverser.foundLoopNode(this, depth))
			return;

		m_loop.traverse(depth + 1, nodeTraverser);			

	}

	public String toString() {
		return "SelfLoop" + m_id;
	}

	public List<Node> getChildren() {
		List<Node> children = new LinkedList<Node>();
		children.add(m_loop);
		return children;
	}

	public Collection<Node> whatsNext(Node node) {
		Collection<Node> next = new HashSet<Node>();
		next.addAll(getParent().whatsNext(this));
		next.add(m_loop);
		return next;
	}
		
	public boolean isLoopNode() {
		return true;
	}
	
	public void setNumIterations(long numIterations) {
	    if (!m_numIterationsDone) {
	    	m_numIterationsDone = true;
	    	
			for (Node child : getChildren()) {
				child.setNumIterations(numIterations);
			}
	    }
	}	
	
	boolean checkBackTrail(Stack<ReferenceType> referenceTypes, Node node, 
			boolean calledFromAbove, FunctionCallStatus functionCallStatus) {
		
		if (!calledFromAbove) {
		    return super.checkBackTrail(referenceTypes, node, calledFromAbove, functionCallStatus);
		}
		return false;
	}
<<<<<<< HEAD:src/com/xmos/xta/engine/analysis/structural/nodes/SelfLoopNode.java
	
	// --------------------------------------------------------------------- //
	// Statistics Gathering Methods
	// --------------------------------------------------------------------- //
	public double getTimeTakenRelative(Case c) {
		return m_loop.getTimeTakenRelative(c);
	}

	public double getTimeTakenAbsolute(Case c) {
		return m_loop.getTimeTakenAbsolute(c);
	}
	
	public long getNumCyclesRelative(Case c) {
		return m_loop.getNumCyclesRelative(c);
	}

	public long getNumCyclesAbsolute(Case c) {
		return m_loop.getNumCyclesAbsolute(c);
	}
	
	public long getNumInstructionsRelative(Case c) {
		return m_loop.getNumInstructionsRelative(c);
	}

	public long getNumInstructionsAbsolute(Case c) {
		return m_loop.getNumInstructionsAbsolute(c);
	}
	
	public long getNumFNopsRelative(Case c) {
		return m_loop.getNumFNopsRelative(c);
	}

	public long getNumFNopsAbsolute(Case c) {
		return m_loop.getNumFNopsAbsolute(c);
	}	
=======


	public List<XtaInstruction> getInstructionStream(Case c) {
		List<XtaInstruction> insts = new LinkedList<XtaInstruction>();
		for(int i=0;i< m_numIterations;i++){
			insts.addAll(m_loop.getInstructionStream(c));
		}
		return insts;
	}		
>>>>>>> 9414cf08dc0923f0a33963a19b05a32314c9336d:src/com/xmos/xta/engine/analysis/structural/nodes/SelfLoopNode.java
}
