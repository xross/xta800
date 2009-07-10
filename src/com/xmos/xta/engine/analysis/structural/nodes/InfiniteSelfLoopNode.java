package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.TreeSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.engine.instructions.XtaInstruction;
import com.xmos.xta.engine.references.types.ReferenceType;

public class InfiniteSelfLoopNode extends Node{
	private NodeAttributes m_attributes;
	private Node m_loop;

	public InfiniteSelfLoopNode(InfiniteSelfLoopNode loop) {
		super();
		m_loop = loop.m_loop;
		m_attributes = new NodeAttributes(this);
	}

	public InfiniteSelfLoopNode() {
		super();
		m_loop = null;
		m_attributes = new NodeAttributes(this);
	}

	public InfiniteSelfLoopNode(Node base) {
		super();
		m_loop = base;
		m_attributes = new NodeAttributes(this);
	}

	public InfiniteSelfLoopNode shallowCopy() {
		return new InfiniteSelfLoopNode(this);
	}

	public Node deepCopy() {
		InfiniteSelfLoopNode ln = new InfiniteSelfLoopNode();
		ln.m_loop = m_loop.deepCopy();
		ln.m_attributes = new NodeAttributes(ln);
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

	public Set<Integer> getPCs() {
		Set<Integer> pcs = new TreeSet<Integer>(m_loop.getPCs());
		return pcs;
	}

	public Collection<Node> getContainedNodes() {
		return m_loop.getContainedNodes();
	}

	public int getStartPc() {
		return m_loop.getStartPc();
	}

	public void traverse(int depth, NodeTraverser nodeTraverser) {
		if (!nodeTraverser.foundInfiniteSelfLoopNode(this, depth))
			return;

		m_loop.traverse(depth + 1, nodeTraverser);			

	}

	public String toString() {
		return "InfiniteSelfLoop" + m_id;
	}

	public List<Node> getChildren() {
		List<Node> children = new LinkedList<Node>();
		children.add(m_loop);
		return children;
	}

	public Collection<Node> whatsNext(Node node) {
		Collection<Node> next = new TreeSet<Node>();		
		next.add(m_loop);
		return next;
	}
		
	boolean checkBackTrail(Stack<ReferenceType> referenceTypes, Node node, 
			boolean calledFromAbove, FunctionCallStatus functionCallStatus) {
		
		if (!calledFromAbove) {
		    return super.checkBackTrail(referenceTypes, node, calledFromAbove, functionCallStatus);
		}
		return false;
	}

	public void getInstructionStream(Case c, List<InstructionNode> instructions) {
		// TODO We need to warn the user here that you cant trace an infinite loop!
	}

	public Collection<Node> whatsBefore(Node node) {
		assert(m_loop == node);
		Collection<Node> before = new TreeSet<Node>();
		before.add(m_loop);
		if(getParent() != null){
			before.addAll(getParent().whatsBefore(this));
		}
		return before;
	}
	public List<Node> getImmediateChildren() {
		List<Node> list = new LinkedList<Node>();
		list.add(m_loop);
		return list;
	}

	public boolean isInfiniteSelfLoopNode() {
		return true;
	}

	public Node getLoop() {
		return m_loop;
	}

	public TimeDistributionBuckets getTimeSpread() {
		TimeDistributionBuckets tdb = new TimeDistributionBuckets();
		tdb.setInfinite();
		return tdb;
	}
}
