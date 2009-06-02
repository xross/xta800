package com.xmos.xta.engine.analysis.nodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.xmos.xta.engine.instructions.XtaInstruction;

public class InstructionNode extends Node {
	private XtaInstruction m_inst;
	
	public InstructionNode(XtaInstruction inst){
		m_inst = inst;
	}
	
	public InstructionNode(InstructionNode node){
		m_inst = node.m_inst;
	}

	public double getTimeTaken(Case c) {
		return m_inst.getTimeTaken();
	}
	
	public long getNumCycles(Case c) {
		return m_inst.getNumCycles();
	}

	public long getNumInstructions(Case c) {
		return 1;
	}
		
	public long getNumFNops(Case c) {
		if (m_inst.isFnop()) {
			return 1;
		} else {
			return 0;
		}
	}
	
	public Node clone() {
		return new InstructionNode(this);
	}

	public String toString(){
		return m_inst.toString();
	}
	
	public Collection<Node> getContaintedNodes() {
		Collection<Node> containedNodes = new HashSet<Node> ();
		containedNodes.add(this);
		return containedNodes;
	}

	public String getFunctionName() {
		return "";
	}
	
	public String getGraphvizName() {
		return "Instruction" + m_id;
	}

	public List<XtaInstruction> getInstructions() {
		LinkedList<XtaInstruction> inst = new LinkedList<XtaInstruction>();
		inst.add(m_inst);
		return inst;
	}

	public Set<Integer> getPCsCovered() {
		Set<Integer> pcs = new HashSet<Integer>();
		pcs.add(m_inst.getPc());
		return pcs;
	}

	public int getStartPc() {
		return m_inst.getPc();
	}

	public boolean isFunction() {
		return false;
	}

	public void traverse(int depth, NodeTraverser nodeTraverser) {
		nodeTraverser.foundInstructionNode(this, depth);		
	}

	public Node deepClone() {
		return clone();
	}		
}
