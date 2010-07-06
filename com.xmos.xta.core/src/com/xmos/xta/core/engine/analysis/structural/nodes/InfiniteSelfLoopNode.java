package com.xmos.xta.core.engine.analysis.structural.nodes;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import com.xmos.xta.core.engine.analysis.binary.XtaInstruction;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.InfiniteSelfLoopNodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverserWithAncestors;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.TraverseStatus;

public class InfiniteSelfLoopNode extends Node{
	private NodeAttributes m_attributes;
	private Node m_loop;

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

	public Node copyRouteStructure() {
		InfiniteSelfLoopNode ln = new InfiniteSelfLoopNode();
		ln.m_loop = m_loop.copyRouteStructure();
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

	public TraverseStatus traverse(Stack<Node> ancestors, NodeTraverserWithAncestors nodeTraverser) {
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

	public TraverseStatus traverse(NodeTraverser nodeTraverser) {
		TraverseStatus status = nodeTraverser.foundInfiniteSelfLoopNode(this);
		if (status == TraverseStatus.CONTINUE) {
			status = m_loop.traverse(nodeTraverser);
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

	public boolean canReplaceChildren() {
		return true;
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
