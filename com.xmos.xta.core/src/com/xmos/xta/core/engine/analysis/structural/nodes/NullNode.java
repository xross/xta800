package com.xmos.xta.core.engine.analysis.structural.nodes;

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

public class NullNode extends Node {
	public NullNode(){
		super();
	}
	
	public Node deepCopy(Aborter aborter) throws AbortedException {
		return new NullNode();
	}
	
	public NodeAttributes getAttributes() {
		assert(false);
		return null;	
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
		return "null";
	}

	public Node copyStructure() {
		return new NullNode();
	}

	public TraverseStatus traverse(Stack<Node> ancestors, NodeTraverser nodeTraverser) {
		return TraverseStatus.CONTINUE;
	}

	public boolean isNullNode() {
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
	//below here the methods have been verified useful
	public Set<Node> dChildren() {
		return new TreeSet<Node>();
	}

	public Set<Node> iChildren() {
		return new TreeSet<Node>();
	}

	public Set<Node> structurGraphNext(Node childNode) {
		assert(false);
		return null;
	}		
	
	public Set<InstructionNode> getInstructionNodes() {
		assert(false);
		return null;
	}
	
	public Set<FunctionReferenceNode> getFunctionReferencesNodes() {
		assert(false);
		return null;
	}
}
