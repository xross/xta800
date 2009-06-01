package com.xmos.xta.engine.analysis.nodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.xmos.xta.engine.instructions.XtaInstruction;

public class LoopNode extends Node {
	
	private List<Node> m_loops;
	private Node m_loopHeader;
	
	public LoopNode(LoopNode loop){
		super();
		m_loops = new ArrayList<Node>(loop.m_loops);
		m_loopHeader = loop.m_loopHeader;
	}
	
	
	public LoopNode(Node loop, Node base) {
		super();
		m_loops = new ArrayList<Node>();
		m_loops.add(loop);
		m_loopHeader = base;
	}
	
	public LoopNode(Set<Node> loops, Node base) {
		super();
		m_loops = new ArrayList<Node>();
		for(Node loop : loops){
			m_loops.add(loop);
		}
		m_loopHeader = base;
	}
	
	public void addLoop(Node loop){
		m_loops.add(loop);
	}

	public List<XtaInstruction> getInstructions() {
		List<XtaInstruction> insts = new ArrayList<XtaInstruction>(m_loopHeader.getInstructions());
		for(Node al : m_loops){
			insts.addAll(al.getInstructions());
		}
		return insts;
	}

	public long getNumberCycles() {
		long count = 0;
		for(Node al : m_loops){
			count += al.getNumberCycles();
		}
		return count;
	}

	public long getNumberFNOPS() {
		long count = 0;
		for(Node al : m_loops){
			count += al.getNumberFNOPS();
		}
		return count;
	}

	public Set<Integer> getPCsCovered() {
		Set<Integer> pcs = new HashSet<Integer>();
		for(Node al : m_loops){
			pcs.addAll(al.getPCsCovered());
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


	public String getGraphvizName() {
		return "cluster_Loop" + m_id;
	}

	public boolean isFunction() {
		return false;
	}

	public int getStartPc() {
		return m_loopHeader.getStartPc();
	}

	public void traverse(int depth, NodeTraverser nodeTraverser) {
	}	
	
	public void display(int indentLevel) {
		printIndent(indentLevel);
		System.out.printf("loop(%d)\n", m_id);	
		printIndent(indentLevel);
		System.out.printf(" header:\n", m_id);
		m_loopHeader.display(indentLevel+1);
		for (Node attachedLoop : m_loops) {
			printIndent(indentLevel);
			System.out.printf(" body:\n", m_id);
			attachedLoop.display(indentLevel + 1);
			
		}		
	}		
}
