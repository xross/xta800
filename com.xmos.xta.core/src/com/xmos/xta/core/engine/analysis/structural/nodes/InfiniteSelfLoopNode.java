package com.xmos.xta.core.engine.analysis.structural.nodes;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.Aborter;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.InfiniteSelfLoopNodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverser.TraverseStatus;
import com.xmos.xta.core.engine.instructions.XtaInstruction;

public class InfiniteSelfLoopNode extends Node{
	private NodeAttributes m_attributes;
	private Node m_loop;

	public InfiniteSelfLoopNode(InfiniteSelfLoopNode loop) {
		super();
		m_loop = loop.m_loop;
		m_attributes = new InfiniteSelfLoopNodeAttributes(this);
	}

	public InfiniteSelfLoopNode() {
		super();
		m_loop = null;
		m_attributes = new InfiniteSelfLoopNodeAttributes(this);
	}

	public InfiniteSelfLoopNode(Node base) {
		super();
		m_loop = base;
		m_attributes = new InfiniteSelfLoopNodeAttributes(this);
	}

	public InfiniteSelfLoopNode copyStructure() {
		return new InfiniteSelfLoopNode(this);
	}

	public Node deepCopy(Aborter aborter) throws AbortedException {
		InfiniteSelfLoopNode ln = new InfiniteSelfLoopNode();
		ln.m_loop = m_loop.deepCopy(aborter);
		ln.m_attributes = new InfiniteSelfLoopNodeAttributes(ln);
		return ln;
	}

    public String getTypeName() {
        return "infiniteselfloop";
    }

    public NodeAttributes getAttributes() {
    	return m_attributes;
    }

	public LinkedList<XtaInstruction> getInstructions() {
	    LinkedList<XtaInstruction> insts = new LinkedList<XtaInstruction>(m_loop.getInstructions());
		return insts;
	}

	public long getStartPc() {
		return m_loop.getStartPc();
	}

	public TraverseStatus traverse(Stack<Node> ancestors, NodeTraverser nodeTraverser) {
		TraverseStatus status = nodeTraverser.foundInfiniteSelfLoopNode(this, ancestors);
		if (status == TraverseStatus.CONTINUE) {
			ancestors.push(this);
			status = m_loop.traverse(ancestors, nodeTraverser);
			ancestors.pop();
		}
		if (status == TraverseStatus.ABORT) {
			return status;
		}
		return TraverseStatus.CONTINUE;
	}

	public boolean isInfiniteSelfLoopNode() {
		return true;
	}

	public TimeDistributionBuckets getTimeSpread() {
		TimeDistributionBuckets tdb = new TimeDistributionBuckets();
		tdb.setInfinite();
		return tdb;
	}

	public void replaceChild(Node oldNode, Node newNode) {
		if (oldNode == m_loop) {
			m_loop = newNode;
		}
	}

	public void replaceChildren(List<Node> children) {
		assert(children.size() == 1);
		m_loop = children.get(0);
	}

	//below here the methods have been verified useful

	public List<Node> iChildren() {
		List<Node> children = new LinkedList<Node>();
		children.add(m_loop);
		return children;
	}

	public Set<Node> structurGraphNext(Node childNode) {
		Set<Node> next = new TreeSet<Node>();
		//TODO check this
		next.add(m_loop);
		return next;
	}

	public Set<InstructionNode> getInstructionNodes() {
		Set<InstructionNode> insts = new TreeSet<InstructionNode>();
		insts.addAll(m_loop.getInstructionNodes());
		return insts;
	}

	public Set<FunctionReferenceNode> getFunctionReferencesNodes() {
		Set<FunctionReferenceNode> fnRef = new TreeSet<FunctionReferenceNode>();
		addFunctionRefToRefSet(fnRef, m_loop);
		return fnRef;
	}

	public void buildListDown(List<Node> nodeList) {
		m_loop.buildListDown(nodeList);
	}

	public void buildListUp(List<Node> nodeList,
			Stack<FunctionReferenceNode> stack, Node start) {
		if(m_parent!= null){
			m_parent.buildListUp(nodeList, stack, this);
		}
	}
}
