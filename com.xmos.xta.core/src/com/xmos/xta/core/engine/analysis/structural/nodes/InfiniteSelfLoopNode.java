package com.xmos.xta.core.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.InfiniteSelfLoopNodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.core.engine.instructions.XtaInstruction;
import com.xmos.xta.core.engine.references.types.ReferenceType;

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

	public InfiniteSelfLoopNode shallowCopy() {
		return new InfiniteSelfLoopNode(this);
	}

	public Node deepCopy() {
		InfiniteSelfLoopNode ln = new InfiniteSelfLoopNode();
		ln.m_loop = m_loop.deepCopy();
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

	public void traverse(Stack<Node> ancestors, NodeTraverser nodeTraverser) {
		if (!nodeTraverser.foundInfiniteSelfLoopNode(this, ancestors))
			return;

		ancestors.push(this);
		m_loop.traverse(ancestors, nodeTraverser);			
		ancestors.pop();
	}

	public List<Node> getChildren() {
		List<Node> children = new LinkedList<Node>();
		children.add(m_loop);
		return children;
	}

	public Collection<NodeDescriptor> whatsNext(NodeDescriptor nodeDescriptor) {
		Collection<NodeDescriptor> next = new TreeSet<NodeDescriptor>();		
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
	
	public List<NodeDescriptor> getImmediateChildren(Stack<Node> ancestors) {
		List<NodeDescriptor> list = new LinkedList<NodeDescriptor>();		
		ancestors.push(this);
	    list.add(new NodeDescriptor(m_loop, ancestors));
		ancestors.pop();
		return list;
	}
	
	public boolean isInfiniteSelfLoopNode() {
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
		TimeDistributionBuckets tdb = new TimeDistributionBuckets();
		tdb.setInfinite();
		return tdb;
	}
	
	boolean checkBackTrail(Stack<ReferenceType> referenceTypes, 
			Stack<Node> ancestors, Node node, boolean calledFromAbove) {
		
		if (!calledFromAbove) {
		    return super.checkBackTrail(referenceTypes, ancestors, node, calledFromAbove);
		}
		return false;
	}
}
