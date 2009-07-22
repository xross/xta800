package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.engine.instructions.XtaInstruction;

public class NullNode extends IBufferNode {
	public NullNode(){
		super();
	}
	
	public Node deepCopy() {
		return new NullNode();
	}
	
	public NodeAttributes getAttributes() {
		assert(false);
		return null;	
	}

	public List<Node> getChildren() {
		return new LinkedList<Node>();
	}

	public Collection<Node> getContainedNodes() {
		return new LinkedList<Node>();
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

	public int getStartPc() {
		return 0;
	}

	public String getTypeName() {
		return "null";
	}

	public Node shallowCopy() {
		return new NullNode();
	}

	public void traverse(int depth, NodeTraverser nodeTraverser) {
		if (!nodeTraverser.foundTerminalNode(this, depth))
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

	public boolean isNullNode() {
		return true;
	}
	
	public TimeDistributionBuckets getTimeSpread() {
		return new TimeDistributionBuckets(0);
	}
	public Set<InstructionNode> getEndNodes() {
		Set<InstructionNode> set = new TreeSet<InstructionNode>();
		return set;
	}
}
