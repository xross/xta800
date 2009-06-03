package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.xmos.xta.engine.instructions.XtaInstruction;

public class LoopNode extends Node {
	private Node m_loopHeader;
	private List<Node> m_loops;
	private long m_numIterations;
	private boolean m_numIterationsSet;
	private boolean m_isAbsolute;
	
	public LoopNode(LoopNode loop) {
		super();
		m_loopHeader = loop.m_loopHeader;
		m_loops = new ArrayList<Node>(loop.m_loops);
		m_numIterations = 0;
		m_numIterationsSet = false;
		m_isAbsolute = false;
	}
	
	public LoopNode() {
		super();
		m_loopHeader = null;
		m_loops = new ArrayList<Node>();
		m_numIterations = 0;
		m_numIterationsSet = false;
		m_isAbsolute = false;
	}
	
	public LoopNode(Node loop, Node base) {
		super();
		m_loopHeader = base;
		m_loops = new ArrayList<Node>();
		m_loops.add(loop);
		m_numIterations = 0;
		m_numIterationsSet = false;
		m_isAbsolute = false;
	}
	
	public LoopNode(Set<Node> loops, Node base) {
		super();
		m_loopHeader = base;
		m_loops = new ArrayList<Node>();
		for (Node loop : loops) {
			m_loops.add(loop);
		}
		m_numIterations = 0;
		m_isAbsolute = false;
		m_numIterationsSet = false;
	}

	public Node deepClone(Map<Integer, FunctionNode> startPcs) {
		LoopNode ln = new LoopNode();
		for (Node loop : m_loops) {
			Node clone = loop.deepClone(startPcs);
			ln.m_loops.add(clone);
		}
		ln.m_loopHeader = m_loopHeader.deepClone(startPcs);
		return ln;
	}	

	public double getTimeTaken(Case c) {
		double time = 0;
		for (Node node : m_loops) {
			time += node.getTimeTaken(c);
		}
		return time;
	}
	
	public long getNumCycles(Case c) {
		long count = 0;
   		for (Node node : m_loops) {
    		count += node.getNumCycles(c);
	    }
		return count;
	}

	public long getNumInstructions(Case c) {
   		return getInstructions().size();
	}

	public long getNumFNops(Case c) {
		long count = 0;
   		for (Node node : m_loops) {
    		count += node.getNumFNops(c);
   		}
		return count;
	}	
	
	public void addLoop(Node loop) {
		m_loops.add(loop);
	}

	public List<XtaInstruction> getInstructions() {
		List<XtaInstruction> insts = new ArrayList<XtaInstruction>(m_loopHeader.getInstructions());
		for(Node al : m_loops){
			insts.addAll(al.getInstructions());
		}
		return insts;
	}

	public Set<Integer> getPCs() {
		Set<Integer> pcs = new HashSet<Integer>(m_loopHeader.getPCs());
		for(Node loop : m_loops){
			pcs.addAll(loop.getPCs());
		}
		return pcs;
	}

	public LoopNode clone() {
		return new LoopNode(this);
	}

	public Collection<Node> getContaintedNodes() {
		Collection<Node> containedNodes = new HashSet<Node> ();
		containedNodes.addAll(m_loopHeader.getContaintedNodes());
		for(Node loop: m_loops){
			containedNodes.addAll(loop.getContaintedNodes());
		}
		return containedNodes;
	}

	public String getFunctionName() {
		return "";
	}
	
	public boolean isFunction() {
		return false;
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

	public String toString(){
		return "Loop" + m_id;
	}
	
	public void setNumIterations(long numIterations) {
		m_numIterations = numIterations;
		setNumIterationsSet(true);
	}
	
	public long getNumIterations() {
		return m_numIterations;
	}

	public void setNumIterationsSet(boolean numIterationsSet) {
		m_numIterationsSet = numIterationsSet;
	}
	
	public boolean getNumIterationsSet() {
		return m_numIterationsSet;
	}
	
	public void setScope(boolean isAbsolute) {
		m_isAbsolute = isAbsolute;
	}
	
	public boolean getScope() {
		return m_isAbsolute;
	}
}
