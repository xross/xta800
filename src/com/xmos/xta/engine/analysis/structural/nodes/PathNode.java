package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.xmos.xta.engine.analysis.controlflow.InstructionBuffer;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.engine.instructions.XtaInstruction;
import com.xmos.xta.engine.references.types.ReferenceType;

public class PathNode extends Node {
	private NodeAttributes m_attributes;
	List<Node> m_nodes;

	public PathNode(List<Node> nodes) {
		super();
		assert (nodes != null);
		assert (nodes.size() > 1);
		m_nodes = nodes;
		m_attributes = new NodeAttributes(this);
	}

	public PathNode(PathNode path) {
		super();
		m_nodes = path.m_nodes;
		m_attributes = new NodeAttributes(this);
	}

	public PathNode() {
		super();
		m_nodes = new LinkedList<Node>();
		m_attributes = new NodeAttributes(this);
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
		pn.m_attributes = new NodeAttributes(pn);
		return pn;
	}

    public String getTypeName() {
        return "path";
    }
    
    public NodeAttributes getAttributes() {
    	return m_attributes;
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

	public void getInstructionStream(Case c, List<InstructionNode> instructions, long iteration) {
		for(Node pathNode : m_nodes){
			pathNode.getInstructionStream(c, instructions, iteration);
		}
	}

	public long getError(Case c) {
		long error = 0;
		for(Node pathNode : m_nodes){
			error += pathNode.getError(c);
		}
		return error;
	}

	public void addFnops() {
		for(Node pathNode : m_nodes){
			pathNode.addFnops();
		}
		Node lastNode = m_nodes.get(m_nodes.size()-1);

		for(InstructionBuffer ib : lastNode.getIbufferDueToWorstTime()){
			getParent().addEndIbufferDueToWorst(ib);
		}
		for(InstructionBuffer ib : lastNode.getIbufferDueToBestTime()){
			getParent().addEndIbufferDueToBest(ib);
		}

	}


	public Collection<Node> whatsBefore(Node node) {
		assert(m_nodes.contains(node));
		int index = m_nodes.indexOf(node);
		if(index > 0){
			Collection<Node> before = new HashSet<Node>();
			before.add(m_nodes.get(index-1));
			return before;
		} else {
			if(getParent()!= null){
				return getParent().whatsBefore(this);
			} else {
				return new HashSet<Node>();
			}
		}
	}
}
