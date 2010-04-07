package com.xmos.xta.core.engine.analysis.structural.nodes;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.Aborter;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverserWithAncestors;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.TraverseStatus;
import com.xmos.xta.core.engine.instructions.XtaInstruction;

public class NullNode extends Node {
	public NullNode(){
		super();
	}

	public Node copyRouteStructure(Aborter aborter) throws AbortedException {
		return new NullNode();
	}

	public NodeAttributes getAttributes() {
		assert(false);
		return null;
	}

	public LinkedList<XtaInstruction> getInstructions() {
		return new LinkedList<XtaInstruction>();
	}

	public long getStartPc() {
		return 0;
	}

	public String getTypeName() {
		return "null";
	}

	public TraverseStatus traverse(Stack<Node> ancestors, NodeTraverserWithAncestors nodeTraverser) {
		return TraverseStatus.CONTINUE;
	}

	public boolean isNullNode() {
		return true;
	}

	public TimeDistributionBuckets getTimeSpread() {
		return new TimeDistributionBuckets(0);
	}

	//below here the methods have been verified useful

	public List<Node> iChildren() {
		return new LinkedList<Node>();
	}

	public void buildListDown(List<Node> nodeList) {

	}
	public void buildListUp(List<Node> nodeList,Node start) {
	}

	public TraverseStatus traverse(NodeTraverser nodeTraverser) {
		return TraverseStatus.CONTINUE;
	}
}
