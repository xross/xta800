package com.xmos.xta.core.engine.analysis.structural.nodes;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.Aborter;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.SelfLoopNodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverserWithAncestors;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.TraverseStatus;
import com.xmos.xta.core.engine.instructions.XtaInstruction;

public class SelfLoopNode extends Node{
	private SelfLoopNodeAttributes m_attributes;
	private Node m_loop;

	public SelfLoopNode(SelfLoopNode loop) {
		super();
		m_loop = loop.m_loop;
		m_attributes = new SelfLoopNodeAttributes(this);
	}

	public SelfLoopNode() {
		super();
		m_loop = null;
		m_attributes = new SelfLoopNodeAttributes(this);
	}

	public SelfLoopNode(Node base) {
		super();
		m_loop = base;
		m_attributes = new SelfLoopNodeAttributes(this);
	}

	public Node copyRouteStructure(Aborter aborter) throws AbortedException {
		SelfLoopNode ln = new SelfLoopNode();
		ln.m_loop = m_loop.copyRouteStructure(aborter);
		ln.m_attributes = new SelfLoopNodeAttributes(ln);
		return ln;
	}
    public String getTypeName() {
        return "selfloop";
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

	public TraverseStatus traverse(Stack<Node> ancestors, NodeTraverserWithAncestors nodeTraverser) {
		TraverseStatus status = nodeTraverser.foundSelfLoopNode(this, ancestors);

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
	
	public TraverseStatus traverse( NodeTraverser nodeTraverser) {
		TraverseStatus status = nodeTraverser.foundSelfLoopNode(this);

		if (status == TraverseStatus.CONTINUE) {
			status = m_loop.traverse(nodeTraverser);
		}

		if (status == TraverseStatus.ABORT) {
			return status;
		}
		return TraverseStatus.CONTINUE;
	}

	public boolean isSelfLoopNode() {
		return true;
	}

	public Node getLoop() {
		return m_loop;
	}

	public TimeDistributionBuckets getTimeSpread() {
		TimeDistributionBuckets tdb = m_loop.getTimeSpread();
		tdb.repeat(getAttributes().getLoopIterations());
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

	public void buildListDown(List<Node> nodeList) {
		m_loop.buildListDown(nodeList);
	}

	public void buildListUp(List<Node> nodeList, Node start) {
		if(m_parent!= null){
			m_parent.buildListUp(nodeList, this);
		}
	}
}
