package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.engine.instructions.XtaInstruction;
import com.xmos.xta.engine.references.types.ReferenceType;

public class PathNode extends Node {
	List<Node> m_nodes;

	public PathNode(List<Node> nodes) {
		super();
		assert (nodes != null);
		assert (nodes.size() > 1);
		m_nodes = nodes;
	}

	public PathNode(PathNode path) {
		super();
		m_nodes = path.m_nodes;
	}

	public PathNode() {
		super();
		m_nodes = new LinkedList<Node>();
	}

	public Node shallowCopy() {
		return new PathNode(this);
	}

	public Node deepCopy(Map<Integer, FunctionNode> startPcs) {
		PathNode pn = new PathNode();
		for (Node path : m_nodes) {
			Node clone = path.deepCopy(startPcs);
			pn.m_nodes.add(clone);
		}
		return pn;
	}

	public LinkedList<XtaInstruction> getInstructions() {
		LinkedList<XtaInstruction> list = new LinkedList<XtaInstruction>();
		for (Node node : m_nodes) {
			list.addAll(node.getInstructions());
		}
		return list;
	}

	public Set<Integer> getPCs() {
		Set<Integer> pcs = new HashSet<Integer>();
		for (Node node : m_nodes) {
			pcs.addAll(node.getPCs());
		}
		return pcs;
	}

	public Collection<Node> getContainedNodes() {
		Collection<Node> containedNodes = new HashSet<Node>();
		for (Node loop : m_nodes) {
			containedNodes.addAll(loop.getContainedNodes());
		}
		return containedNodes;
	}

	public String getFunctionName() {
		return "";
	}

	public int getStartPc() {
		return m_nodes.get(0).getStartPc();
	}

	public void traverse(int depth, NodeTraverser nodeTraverser) {
		if (!nodeTraverser.foundPathNode(this, depth))
			return;

		for (Node node : m_nodes) {
			node.traverse(depth + 1, nodeTraverser);
		}
	}

	public String toString() {
		return "Path" + m_id;
	}

	public List<Node> getChildren() {
		return m_nodes;
	}

	public Collection<Node> whatsNext(Node node) {
		assert (m_nodes.contains(node));
		int index = m_nodes.indexOf(node);
		if (index < m_nodes.size() - 1) {
			Collection<Node> next = new HashSet<Node>();
			next.add(m_nodes.get(index + 1));
			return next;
		} else {
			return getParent().whatsNext(this);
		}
	}

	boolean checkBackTrail(Stack<ReferenceType> referenceTypes, Node node, boolean calledFromAbove,
			FunctionCallStatus functionCallStatus) {

		if (!calledFromAbove) {
			return super.checkBackTrail(referenceTypes, node, calledFromAbove, functionCallStatus);
		}
		return false;
	}

	public void getInstructionStream(Case c, List<XtaInstruction> instructions) {

	}

	// --------------------------------------------------------------------- //
	// Statistics Gathering Methods
	// --------------------------------------------------------------------- //
	public double getTimeTakenRelative(Case c) {
		double time = 0;
		for (Node node : m_nodes) {
			time += node.getTimeTakenRelative(c);
		}
		return time;
	}

	public double getTimeTakenAbsolute(Case c) {
		double time = 0;
		for (Node node : m_nodes) {
			time += node.getTimeTakenAbsolute(c);
		}
		return time;
	}

	public long getNumCyclesRelative(Case c) {
		long count = 0;
		for (Node node : m_nodes) {
			count += node.getNumCyclesRelative(c);
		}
		return count;
	}

	public long getNumCyclesAbsolute(Case c) {
		long count = 0;
		for (Node node : m_nodes) {
			count += node.getNumCyclesAbsolute(c);
		}
		return count;
	}

	public long getNumInstructionsRelative(Case c) {
		long count = 0;
		for (Node node : m_nodes) {
			count += node.getNumInstructionsRelative(c);
		}
		return count;
	}

	public long getNumInstructionsAbsolute(Case c) {
		long count = 0;
		for (Node node : m_nodes) {
			count += node.getNumInstructionsAbsolute(c);
		}
		return count;
	}

	public long getNumFNopsRelative(Case c) {
		long fnops = 0;
		for (Node node : m_nodes) {
			fnops += node.getNumFNopsRelative(c);
		}
		return fnops;
	}

	public long getNumFNopsAbsolute(Case c) {
		long fnops = 0;
		for (Node node : m_nodes) {
			fnops += node.getNumFNopsRelative(c);
		}
		return fnops;
	}

	public void updateFnops(Case c, IBufferVector predessorIBufferVector) {
		IBufferVector endVector = new IBufferVector();
		
		for(Node pathNode : m_nodes){
			
		}
		
		
		
	}
}
