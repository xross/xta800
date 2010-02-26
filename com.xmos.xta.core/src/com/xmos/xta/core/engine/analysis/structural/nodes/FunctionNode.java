package com.xmos.xta.core.engine.analysis.structural.nodes;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.Aborter;
import com.xmos.xta.core.engine.TimeUnit;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverserWithAncestors;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.TraverseStatus;
import com.xmos.xta.core.engine.instructions.XtaInstruction;

public class FunctionNode extends Node {
	private NodeAttributes m_attributes;
	private String m_functionName;
	private List<Node> m_functionSequence;
	private static int functionId;

	public FunctionNode() {
		super();
		m_attributes = new NodeAttributes(this);
		setUnresolved(true);
	}

	public FunctionNode(Node function, String functionName) {
		super();
		if (function.isSequenceNode()){
			SequenceNode sn = (SequenceNode)function;

			m_functionSequence = sn.getPath();
		} else {
			m_functionSequence = new LinkedList<Node>();
			m_functionSequence.add(function);
		}

		if (functionName == null){
			m_functionName = "Unnamed function " + functionId++;
		} else {
			m_functionName = functionName;
		}
		m_attributes = new NodeAttributes(this);
	}

	public FunctionNode(FunctionNode fn){
		m_functionName = fn.m_functionName;
		m_functionSequence = fn.m_functionSequence;
		m_attributes = new NodeAttributes(this);
		setUnresolved(fn.isUnresolved());
	}

	public String toString(){
		if(m_functionName!= null){
			return "Function: " + m_functionName;
		} else {
			return getDescription();
		}

	}

	public Node copyStructure() {
    	return new FunctionNode(this);
	}

	public Node deepCopy(Aborter aborter) throws AbortedException {
		aborter.getAbort();

		FunctionNode fn = new FunctionNode();
		fn.m_functionName = m_functionName;
		fn.m_attributes = new NodeAttributes(fn);
		fn.setUnresolved(isUnresolved());
		if (m_functionSequence != null) {
			List<Node> nodes = new LinkedList<Node>();
			for (Node node : m_functionSequence){
				nodes.add(node.deepCopy(aborter));
			}
			fn.m_functionSequence = nodes;
		}
		return fn;
	}

	public String getTypeName() {
        return "function";
    }

    public NodeAttributes getAttributes() {
    	return m_attributes;
    }

	public void setFunction(String functionName, List<Node> fn) {
		m_functionSequence = fn;
		m_functionName = functionName;
	}

	public List<Node> getFunction() {
		return m_functionSequence;
	}

	public String getDescription() {
		if (m_functionName == null) {
			m_functionName = "Unnamed function " + functionId++;
		}
		return m_functionName;
	}

	public String getFunctionName() {
		if (m_functionName == null) {
			m_functionName = "Unnamed function " + functionId++;
		}
		return m_functionName;
	}

	public LinkedList<XtaInstruction> getInstructions() {
		if (m_functionSequence != null) {
			LinkedList<XtaInstruction> list = new LinkedList<XtaInstruction>();
			for (Node node : m_functionSequence) {
				list.addAll(node.getInstructions());
			}
			return list;
		} else {
			return new LinkedList<XtaInstruction>();
		}
	}

	public long getStartPc() {
		if (m_functionSequence != null){
			return m_functionSequence.get(0).getStartPc();
		}
		return 0;
	}

	public TraverseStatus traverse(Stack<Node> ancestors, NodeTraverserWithAncestors nodeTraverser) {
		TraverseStatus status = nodeTraverser.foundFunctionNode(this, ancestors);
		if (status == TraverseStatus.CONTINUE) {
			if (m_functionSequence != null) {
				ancestors.add(this);
				for (Node node : m_functionSequence) {
					status = node.traverse(ancestors, nodeTraverser);
					if (status != TraverseStatus.CONTINUE) {
						break;
					}
				}
				ancestors.remove(this);
			}
		}

		if (status == TraverseStatus.ABORT) {
			return status;
		}
		return TraverseStatus.CONTINUE;
	}

	public TraverseStatus traverse(NodeTraverser nodeTraverser) {
		TraverseStatus status = nodeTraverser.foundFunctionNode(this);
		if (status == TraverseStatus.CONTINUE) {
			if (m_functionSequence != null) {
				for (Node node : m_functionSequence) {
					status = node.traverse( nodeTraverser);
					if (status != TraverseStatus.CONTINUE) {
						break;
					}
				}
			}
		}

		if (status == TraverseStatus.ABORT) {
			return status;
		}
		return TraverseStatus.CONTINUE;
	}
	public boolean isFunctionNode() {
		return true;
	}

	public String getBackTrace(String backTrace) {
		return super.getBackTrace(m_functionName + ":" + backTrace);
	}

	public TimeDistributionBuckets getTimeSpread() {
		TimeUnit fixedTime = m_attributes.getFixedTime();
		if (fixedTime != null) {
			return new TimeDistributionBuckets(fixedTime.getCycles());
		}

		TimeDistributionBuckets tdb = new TimeDistributionBuckets();
		List<TimeDistributionBuckets> tsbSequence = new LinkedList<TimeDistributionBuckets>();
		if (m_functionSequence != null && !m_functionSequence.isEmpty()) {
			for (Node node : m_functionSequence) {
				tsbSequence.add(node.getTimeSpread());
			}
		} else {
			TimeDistributionBuckets infinite = new TimeDistributionBuckets(0);
			infinite.setInfinite();
			tsbSequence.add(infinite);
		}
		tdb.add(tsbSequence);
		return tdb;
	}

	public void setFunctionName(String string) {
		m_functionName = string;
	}

	public void replaceChild(Node oldNode, Node newNode) {
		int index = m_functionSequence.indexOf(oldNode);
		m_functionSequence.remove(index);
		m_functionSequence.add(index, newNode);
	}

	public void replaceChildren(List<Node> children) {
		m_functionSequence.clear();
		m_functionSequence.addAll(children);
	}

	public List<Node> iChildren() {
		if(m_functionSequence!= null){
			return m_functionSequence;
		} else {
			return new LinkedList<Node>();
		}

	}

	public Set<Node> structurGraphNext(Node childNode) {
		assert (m_functionSequence.contains(childNode));
		Set<Node> next = new TreeSet<Node>();
		int index = m_functionSequence.indexOf(childNode);
		if (index < m_functionSequence.size() - 1) {
			next.add(m_functionSequence.get(index + 1));
			return next;
		} else if (childNode.isInfiniteLoopNode() ||
				childNode.isInfiniteSelfLoopNode()) {
			next.add(childNode);
			return next;
		} else {
			//this shouldn't be called as the route construction should have caught it
			assert(false);
			return next;
		}
	}

	public Set<InstructionNode> getInstructionNodes() {
		Set<InstructionNode> insts = new TreeSet<InstructionNode>();
		for (Node node : m_functionSequence) {
			insts.addAll(node.getInstructionNodes());
		}
		return insts;
	}

	public Set<FunctionReferenceNode> getFunctionReferencesNodes() {
		Set<FunctionReferenceNode> fnRef = new TreeSet<FunctionReferenceNode>();
		for(Node node : m_functionSequence){
			addFunctionRefToRefSet(fnRef, node);
		}
		return fnRef;
	}

	public void buildListUp(List<Node> nodeList, Stack<FunctionReferenceNode> stack, Node start) {
		Stack<FunctionReferenceNode> copyStack = new Stack<FunctionReferenceNode>();
		copyStack.addAll(stack);
		if(!copyStack.isEmpty()){
			FunctionReferenceNode parent = copyStack.pop().getAlias();
			if(parent!= null){
				parent.buildListUp(nodeList, copyStack, this);
			}

		}
		for(Node node : m_functionSequence){
			if(start == node) break;
			node.buildListDown(nodeList);
		}

	}

	public void buildListDown(List<Node> nodeList) {
		for(Node node : m_functionSequence){
			node.buildListDown(nodeList);
		}
	}

}
