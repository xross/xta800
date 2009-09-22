package com.xmos.xta.core.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.core.engine.instructions.XtaInstruction;

public class EndNode extends IBufferNode {
	
	private NodeAttributes m_nodeAttributes;
	public EndNode(){
		super();
		m_nodeAttributes = new NodeAttributes(this);
	}
	
	public Node deepCopy() {
		return new EndNode();
	}
	
	public NodeAttributes getAttributes() {
		return m_nodeAttributes;	
	}

	public List<Node> getChildren() {
		return new LinkedList<Node>();
	}

	public Collection<InstructionNode> getContainedNodes() {
		return new LinkedList<InstructionNode>();
	}

	public List<Node> getImmediateChildren() {
		return new LinkedList<Node>();
	}

	public LinkedList<XtaInstruction> getInstructions() {
		return new LinkedList<XtaInstruction>();
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

	public Node shallowCopy() {
		return new EndNode();
	}

	public void traverse(int depth, NodeTraverser nodeTraverser) {
		if (!nodeTraverser.foundEndNode(this, depth))
			return;
	}
	
	public Collection<Node> whatsBefore(Node node) {
		if(getParent() != null){
			return getParent().whatsBefore(this);
		} else {
			return new TreeSet<Node>();
		}
	}

	public Collection<Node> whatsNext(Node node) {
		if (getParent() != null){
			return getParent().whatsNext(this);
		}
		return new TreeSet<Node>();
	}


	public void insertFnops() {
		
	}

	public boolean isEndNode() {
		return true;
	}
	
	public TimeDistributionBuckets getTimeSpread() {
		return new TimeDistributionBuckets(0);
	}
	public Set<InstructionNode> getEndInstructions() {
		Set<InstructionNode> set = new TreeSet<InstructionNode>();
		return set;
	}

	public Set<InstructionNode> getStartInstructions() {
		Set<InstructionNode> set = new TreeSet<InstructionNode>();
		return set;
	}
}
