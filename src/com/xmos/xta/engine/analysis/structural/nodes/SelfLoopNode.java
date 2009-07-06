package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.TreeSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.SelfLoopNodeAttributes;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.engine.instructions.XtaInstruction;
import com.xmos.xta.engine.references.types.ReferenceType;

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

	public SelfLoopNode shallowCopy() {
		return new SelfLoopNode(this);
	}

	public Node deepCopy() {
		SelfLoopNode ln = new SelfLoopNode();
		ln.m_loop = m_loop.deepCopy();
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
		if (!nodeTraverser.foundSelfLoopNode(this, depth))
			return;

		m_loop.traverse(depth + 1, nodeTraverser);			

	}

	public String toString() {
		return "SelfLoop" + m_id;
	}

	public List<Node> getChildren() {
		List<Node> children = new LinkedList<Node>();
		children.add(m_loop);
		return children;
	}

	public Collection<Node> whatsNext(Node node) {
		Collection<Node> next = new TreeSet<Node>();		
		if (getParent() != null){
			next.addAll(getParent().whatsNext(this));
		}
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
		for(int i=0;i< getAttributes().getLoopIterations();i++){
			m_loop.getInstructionStream(c, instructions);
		}
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
		
	public boolean isSelfLoopNode() {
		return true;
	}

	public Node getLoop() {
		return m_loop;
	}
}
