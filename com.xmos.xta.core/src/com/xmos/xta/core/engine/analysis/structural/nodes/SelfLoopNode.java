package com.xmos.xta.core.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.SelfLoopNodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.core.engine.instructions.XtaInstruction;
import com.xmos.xta.core.engine.references.types.ReferenceType;

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

	public long getStartPc() {
		return m_loop.getStartPc();
	}

	public void traverse(Stack<Node> ancestors, NodeTraverser nodeTraverser) {
		if (!nodeTraverser.foundSelfLoopNode(this, ancestors))
			return;

		ancestors.push(this);
		m_loop.traverse(ancestors, nodeTraverser);			
		ancestors.pop();
	}

	public List<Node> getIndirectChildren() {
		List<Node> children = new LinkedList<Node>();
		children.add(m_loop);
		return children;
	}

	public Collection<NodeDescriptor> whatsNext(NodeDescriptor nodeDescriptor) {
		Collection<NodeDescriptor> next = new TreeSet<NodeDescriptor>();
		
		NodeDescriptor thisNodeDescriptor = new NodeDescriptor(this, nodeDescriptor.getAncestors());
		thisNodeDescriptor.getAncestors().pop();

		if (!thisNodeDescriptor.getAncestors().isEmpty()) {
			next.addAll(thisNodeDescriptor.getAncestors().peek().whatsNext(thisNodeDescriptor));
		}
		next.add(new NodeDescriptor(m_loop, nodeDescriptor.getAncestors()));
		return next;
	}
		
	public Collection<NodeDescriptor> whatsBefore(NodeDescriptor nodeDescriptor) {
		assert(m_loop == nodeDescriptor.getNode());
		
		Collection<NodeDescriptor> before = new TreeSet<NodeDescriptor>();
		before.add(new NodeDescriptor(m_loop, nodeDescriptor.getAncestors()));
		
		NodeDescriptor thisNodeDescriptor = new NodeDescriptor(this, nodeDescriptor.getAncestors());
		thisNodeDescriptor.getAncestors().pop();

		if (!thisNodeDescriptor.getAncestors().isEmpty()) {
			before.addAll(thisNodeDescriptor.getAncestors().peek().whatsBefore(thisNodeDescriptor));
		}
		return before;
	}
	
	public List<NodeDescriptor> getDirectChildren(Stack<Node> ancestors) {
		List<NodeDescriptor> list = new LinkedList<NodeDescriptor>();		
		ancestors.push(this);
	    list.add(new NodeDescriptor(m_loop, ancestors));
		ancestors.pop();
		return list;
	}	
		
	public boolean isSelfLoopNode() {
		return true;
	}

	public Node getLoop() {
		return m_loop;
	}

	public Set<NodeDescriptor> getEndInstructions(Stack<Node> ancestors) {
		Set<NodeDescriptor> set = new TreeSet<NodeDescriptor>();
		ancestors.push(this);
		set.addAll(m_loop.getEndInstructions(ancestors));
		ancestors.pop();
		return set;
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
	
	boolean checkBackTrail(Stack<ReferenceType> referenceTypes,
			Stack<Node> ancestors, Node node, boolean calledFromAbove) {
		
		if (!calledFromAbove) {
		    return super.checkBackTrail(referenceTypes, ancestors, node, calledFromAbove);
		}
		return false;
	}
}
