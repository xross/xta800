package com.xmos.xta.core.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.Aborter;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverser.TraverseStatus;
import com.xmos.xta.core.engine.instructions.XtaInstruction;

public class EndNode extends Node {
	private NodeAttributes m_nodeAttributes;
	
	public EndNode(){
		super();
		m_nodeAttributes = new NodeAttributes(this);
	}
	
	public Node deepCopy(Aborter aborter) throws AbortedException {
		return new EndNode();
	}
	
	public NodeAttributes getAttributes() {
		return m_nodeAttributes;	
	}

	public List<Node> getIndirectChildren() {
		return new LinkedList<Node>();
	}

	public List<NodeDescriptor> getDirectChildren(Stack<Node> ancestors) {
		return new LinkedList<NodeDescriptor>();
	}

	public LinkedList<XtaInstruction> getInstructions() {
		return new LinkedList<XtaInstruction>();
	}

	public XtaInstruction getStartInstruction() {
		return null;
	}

	public Set<Integer> getPCs() {
		return new TreeSet<Integer>();
	}

	public long getStartPc() {
		return 0;
	}

	public String getTypeName() {
		return "end";
	}

	public Node copyStructure() {
		return new EndNode();
	}

	public TraverseStatus traverse(Stack<Node> ancestors, NodeTraverser nodeTraverser) {
		TraverseStatus status = nodeTraverser.foundEndNode(this, ancestors);
		if (status == TraverseStatus.ABORT) {
			return status;
		}
		return TraverseStatus.CONTINUE;
	}

	public Collection<NodeDescriptor> whatsNext(NodeDescriptor nodeDescriptor) {
		NodeDescriptor thisNodeDescriptor = new NodeDescriptor(this, nodeDescriptor.getAncestors());
		thisNodeDescriptor.getAncestors().pop();

		if (!thisNodeDescriptor.getAncestors().isEmpty()) {
			return thisNodeDescriptor.getAncestors().peek().whatsNext(thisNodeDescriptor);
		}
		return new TreeSet<NodeDescriptor>();
	}

	public Collection<NodeDescriptor> whatsBefore(NodeDescriptor nodeDescriptor) {
		NodeDescriptor thisNodeDescriptor = new NodeDescriptor(this, nodeDescriptor.getAncestors());
		thisNodeDescriptor.getAncestors().pop();

		if (!thisNodeDescriptor.getAncestors().isEmpty()) {
			return thisNodeDescriptor.getAncestors().peek().whatsBefore(thisNodeDescriptor);
		}
		return new TreeSet<NodeDescriptor>();
	}
	
	public void insertFnops() {
		
	}

	public boolean isEndNode() {
		return true;
	}
	
	public Set<NodeDescriptor> getEndInstructions(Stack<Node> ancestors) {
		Set<NodeDescriptor> set = new TreeSet<NodeDescriptor>();
		return set;
	}
	
	public TimeDistributionBuckets getTimeSpread() {
		return new TimeDistributionBuckets(0);
	}
	
	public void replaceChild(Node oldNode, Node newNode) {
	}	
	
	public void replaceChildren(List<Node> children) {
	}		
}
