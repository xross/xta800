package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.xmos.xta.engine.analysis.structural.nodes.attributes.InfiniteLoopNodeAttributes;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.engine.instructions.XtaInstruction;
import com.xmos.xta.engine.references.types.ReferenceType;

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

	public void traverse(int depth, NodeTraverser nodeTraverser) {
		if (!nodeTraverser.foundInfiniteLoopNode(this, depth))
			return;

		m_loopHeader.traverse(depth + 1, nodeTraverser);			
		for (Node node : m_loops) {
			node.traverse(depth + 1, nodeTraverser);
		}
	}

	public List<Node> getChildren() {
		List<Node> children = new LinkedList<Node>();
		children.add(m_loopHeader);
		for (Node node : m_loops) {
			children.add(node);					
		}
		return children;
	}

	public Collection<Node> whatsNext(Node node) {
		Collection<Node> next = new TreeSet<Node>();
		if (m_loopHeader == node) {
			next.addAll(m_loops);
		} else {
			next.add(this);
		}
		return next;
	}
		
	public boolean isInfiniteLoopNode() {
		return true;
	}
		
	public Collection<Node> whatsBefore(Node node) {
		Collection<Node> before = new TreeSet<Node>();
		if(node == m_loopHeader){
			before.addAll(m_loops);
			if(getParent()!= null){
				before.addAll(getParent().whatsBefore(this));
			}
		} else if(m_loops.contains(node)){
			before.add(m_loopHeader);
		}
		return before;
	}
	public List<Node> getImmediateChildren() {
		List<Node> list = new LinkedList<Node>();
		list.add(m_loopHeader);
		return list;
	}


	public TimeDistributionBuckets getTimeSpread() {
		TimeDistributionBuckets tdb = new TimeDistributionBuckets();
		tdb.setInfinite();
		return tdb;
	}
	
	public Set<InstructionNode> getEndInstructions() {
		Set<InstructionNode> set = new TreeSet<InstructionNode>();
		
		//TODO this need verifying as does it for loop and getStartInstructions()
		//for(Node loop : m_loops){
		//	set.addAll(loop.getEndNodes());
		//}
		set.addAll(m_loopHeader.getEndInstructions());
		return set;
	}
	
	boolean checkBackTrail(Stack<ReferenceType> referenceTypes, Node node, boolean calledFromAbove) {		
		if (!calledFromAbove) {
		    return super.checkBackTrail(referenceTypes, node, calledFromAbove);
		}
		return false;
	}

	public Set<InstructionNode> getStartInstructions() {
		Set<InstructionNode> set = new TreeSet<InstructionNode>();
		//for(Node loop : m_loops){
		//	set.addAll(getStartInstructions());
		//}
		set.addAll(m_loopHeader.getStartInstructions());
		return set;
	}	
}
