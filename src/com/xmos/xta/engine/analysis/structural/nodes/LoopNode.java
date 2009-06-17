package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.xmos.xta.engine.analysis.controlflow.InstructionBuffer;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.LoopNodeAttributes;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.engine.instructions.XtaInstruction;
import com.xmos.xta.engine.references.types.ReferenceType;

public class LoopNode extends Node {
	private LoopNodeAttributes m_attributes;	
	private Node m_loopHeader;
	private List<Node> m_loops;

	public LoopNode(LoopNode loop) {
		super();
		m_loopHeader = loop.m_loopHeader;
		m_loops = new ArrayList<Node>(loop.m_loops);
		m_attributes = new LoopNodeAttributes(this);
	}

	public LoopNode() {
		super();
		m_loopHeader = null;
		m_loops = new ArrayList<Node>();
		m_attributes = new LoopNodeAttributes(this);
	}

	public LoopNode(Node loop, Node base) {
		super();
		m_loopHeader = base;
		m_loops = new ArrayList<Node>();
		m_loops.add(loop);
		m_attributes = new LoopNodeAttributes(this);
	}

	public LoopNode(Set<Node> loops, Node base) {
		super();
		m_loopHeader = base;
		m_loops = new ArrayList<Node>();
		for (Node loop : loops) {
			m_loops.add(loop);
		}
		m_attributes = new LoopNodeAttributes(this);
	}

	public LoopNode shallowCopy() {
		return new LoopNode(this);
	}
	
	public Node deepCopy(Map<Integer, FunctionNode> startPcs) {
		LoopNode ln = new LoopNode();
		for (Node loop : m_loops) {
			Node clone = loop.deepCopy(startPcs);
			ln.m_loops.add(clone);
		}
		ln.m_loopHeader = m_loopHeader.deepCopy(startPcs);
		ln.m_loopHeader.getAttributes().setIsLoopHeader(true);
		ln.m_attributes = new LoopNodeAttributes(ln);
		return ln;
	}

    public String getTypeName() {
        return "loop";
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

	public Set<Integer> getPCs() {
		Set<Integer> pcs = new HashSet<Integer>(m_loopHeader.getPCs());
		for (Node loop : m_loops) {
			pcs.addAll(loop.getPCs());
		}
		return pcs;
	}

	public Collection<Node> getContainedNodes() {
		Collection<Node> containedNodes = new HashSet<Node>();
		containedNodes.addAll(m_loopHeader.getContainedNodes());
		for (Node loop : m_loops) {
			containedNodes.addAll(loop.getContainedNodes());
		}
		return containedNodes;
	}

	public int getStartPc() {
		return m_loopHeader.getStartPc();
	}

	public void traverse(int depth, NodeTraverser nodeTraverser) {
		if (!nodeTraverser.foundLoopNode(this, depth))
			return;

		m_loopHeader.traverse(depth + 1, nodeTraverser);			
		for (Node node : m_loops) {
			node.traverse(depth + 1, nodeTraverser);
		}
	}

	public String toString() {
		return "Loop" + m_id;
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
		Collection<Node> next = new HashSet<Node>();
		if (m_loopHeader == node) {
			next.addAll(getParent().whatsNext(this));
			next.addAll(m_loops);
		} else {
			next.add(this);
		}
		return next;
	}
		
	public boolean isLoopNode() {
		return true;
	}
		
	boolean checkBackTrail(Stack<ReferenceType> referenceTypes, Node node, 
			boolean calledFromAbove, FunctionCallStatus functionCallStatus) {
		
		if (!calledFromAbove) {
		    return super.checkBackTrail(referenceTypes, node, calledFromAbove, functionCallStatus);
		}
		return false;
	}

	public void getInstructionStream(Case c, List<InstructionNode> instructions, long iteration) {
		
		for(Node loop : m_loops){
			for(long i=0;i<loop.getAttributes().getNumIterations(c);i++){
				m_loopHeader.getInstructionStream(c, instructions, i);
				loop.getInstructionStream(c, instructions, i);
			}
		}
		m_loopHeader.getInstructionStream(c, instructions, iteration);
	}

	public long getError(Case c) {
		long error = 0;
		for(Node loop : m_loops){
			error += loop.getAttributes().getNumIterations(c) * loop.getError(c);
		}
		return error;
	}

	public void addFnops() {
		m_loopHeader.addFnops();
		for(Node loop : m_loops){
			loop.addFnops();
		}
		if(getParent()!= null){
			for(InstructionBuffer ib : m_loopHeader.getIbufferDueToWorstTime()){
				getParent().addEndIbufferDueToWorst(ib);
			}
			for(InstructionBuffer ib : m_loopHeader.getIbufferDueToBestTime()){
				getParent().addEndIbufferDueToBest(ib);
			}
		}
	}

	public Collection<Node> whatsBefore(Node node) {
		Collection<Node> before = new HashSet<Node>();
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
}
