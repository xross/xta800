package com.xmos.xta.core.engine.analysis.structural.nodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.InfiniteLoopNodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.core.engine.instructions.XtaInstruction;
import com.xmos.xta.core.engine.references.types.ReferenceType;

public class InfiniteLoopNode extends Node {
	private NodeAttributes m_attributes;
	private Node m_loopHeader;
	private List<Node> m_loops;

	public InfiniteLoopNode(InfiniteLoopNode loop) {
		super();
		m_loopHeader = loop.m_loopHeader;
		m_loops = new ArrayList<Node>(loop.m_loops);
		m_attributes = new InfiniteLoopNodeAttributes(this);
	}

	public InfiniteLoopNode() {
		super();
		m_loopHeader = null;
		m_loops = new ArrayList<Node>();
		m_attributes = new InfiniteLoopNodeAttributes(this);
	}

	public InfiniteLoopNode(Node loop, Node base) {
		super();
		m_loopHeader = base;
		m_loops = new ArrayList<Node>();
		m_loops.add(loop);
		m_attributes = new InfiniteLoopNodeAttributes(this);
	}

	public InfiniteLoopNode(Set<Node> loops, Node base) {
		super();
		m_loopHeader = base;
		m_loops = new ArrayList<Node>();
		for (Node loop : loops) {
			m_loops.add(loop);
		}
		m_attributes = new InfiniteLoopNodeAttributes(this);
	}

	public InfiniteLoopNode shallowCopy() {
		return new InfiniteLoopNode(this);
	}
	
	public Node deepCopy() {
		InfiniteLoopNode ln = new InfiniteLoopNode();
		for (Node loop : m_loops) {
			Node clone = loop.deepCopy();
			ln.m_loops.add(clone);
		}
		ln.m_loopHeader = m_loopHeader.deepCopy();
		ln.m_loopHeader.getAttributes().setIsLoopHeader(true);
		ln.m_attributes = new InfiniteLoopNodeAttributes(ln);
		return ln;
	}

    public String getTypeName() {
        return "infiniteloop";
    }
		
    public NodeAttributes getAttributes() {
    	return m_attributes;
    }
    
	public void addLoop(Node loop) {
		m_loops.add(loop);
	}

	public Node getLoopHeader() {
		return m_loopHeader;
	}
	
	public List<Node> getLoopBodies() {
		return m_loops;
	}
	
	public LinkedList<XtaInstruction> getInstructions() {
		LinkedList<XtaInstruction> insts = new LinkedList<XtaInstruction>(m_loopHeader.getInstructions());
		for (Node al : m_loops) {
			insts.addAll(al.getInstructions());
		}
		return insts;
	}

	public long getStartPc() {
		return m_loopHeader.getStartPc();
	}

	public void traverse(Stack<Node> ancestors, NodeTraverser nodeTraverser) {
		if (!nodeTraverser.foundInfiniteLoopNode(this, ancestors))
			return;

		ancestors.push(this);			
		m_loopHeader.traverse(ancestors, nodeTraverser);			
		for (Node node : m_loops) {
			node.traverse(ancestors, nodeTraverser);
		}
		ancestors.pop();
	}

	public List<Node> getIndirectChildren() {
		List<Node> children = new LinkedList<Node>();
		children.add(m_loopHeader);
		for (Node node : m_loops) {
			children.add(node);					
		}
		return children;
	}

	public boolean isInfiniteLoopNode() {
		return true;
	}
	
	public Collection<NodeDescriptor> whatsNext(NodeDescriptor nodeDescriptor) {
		Collection<NodeDescriptor> next = new TreeSet<NodeDescriptor>();
		if (m_loopHeader == nodeDescriptor.getNode()) {
			for (Node loop : m_loops) {
			    next.add(new NodeDescriptor(loop, nodeDescriptor.getAncestors()));
			}
			
		} else {
			NodeDescriptor thisNodeDescriptor = new NodeDescriptor(this, nodeDescriptor.getAncestors());
			thisNodeDescriptor.getAncestors().pop();			
			next.add(thisNodeDescriptor);
		}
		return next;
	}

	public Collection<NodeDescriptor> whatsBefore(NodeDescriptor nodeDescriptor) {
		Collection<NodeDescriptor> before = new TreeSet<NodeDescriptor>();
		if (m_loopHeader == nodeDescriptor.getNode()) {
			for (Node loop : m_loops) {
				before.add(new NodeDescriptor(loop, nodeDescriptor.getAncestors()));
			}
			
			NodeDescriptor thisNodeDescriptor = new NodeDescriptor(this, nodeDescriptor.getAncestors());
			thisNodeDescriptor.getAncestors().pop();

			if (!thisNodeDescriptor.getAncestors().isEmpty()) {
				before.addAll(thisNodeDescriptor.getAncestors().peek().whatsBefore(thisNodeDescriptor));
			}			
			
		} else if (m_loops.contains(nodeDescriptor.getNode())) {
			before.add(new NodeDescriptor(m_loopHeader, nodeDescriptor.getAncestors()));
		}
		return before;
	}
	
	public List<NodeDescriptor> getDirectChildren(Stack<Node> ancestors) {
		List<NodeDescriptor> list = new LinkedList<NodeDescriptor>();		
		ancestors.push(this);
	    list.add(new NodeDescriptor(m_loopHeader, ancestors));
		ancestors.pop();
		return list;
	}
	
	public Set<NodeDescriptor> getEndInstructions(Stack<Node> ancestors) {
		Set<NodeDescriptor> set = new TreeSet<NodeDescriptor>();	
		ancestors.push(this);
		set.addAll(m_loopHeader.getEndInstructions(ancestors));
		ancestors.pop();
		return set;
	}

	public TimeDistributionBuckets getTimeSpread() {
		TimeDistributionBuckets tdb = new TimeDistributionBuckets();
		tdb.setInfinite();
		return tdb;
	}
		
	public void replaceChild(Node oldNode, Node newNode) {
		if (oldNode == m_loopHeader) {
			m_loopHeader = newNode;
				
		} else {	
		    int index = m_loops.indexOf(oldNode);
		    m_loops.remove(index);
		    m_loops.add(index, newNode);
		}
	}	

	public void replaceChildren(List<Node> children) {
		if (!children.isEmpty()) {
			m_loopHeader = children.get(0);		
			for (int index = 1; index < children.size(); ++index) {
				m_loops.add(children.get(index));
			}			
		}		
	}	
		
	boolean checkBackTrail(Stack<ReferenceType> referenceTypes, 
			Stack<Node> ancestors, Node node, boolean calledFromAbove) {
		
		if (!calledFromAbove) {
		    return super.checkBackTrail(referenceTypes, ancestors, node, calledFromAbove);
		}
		return false;
	}
}
