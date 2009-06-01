package com.xmos.xta.engine.analysis.nodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.xmos.xta.engine.instructions.XtaInstruction;

public class InstructionNode extends Node {

	private XtaInstruction m_inst;
	
	public InstructionNode (XtaInstruction inst){
		m_inst = inst;
	}
	
	public InstructionNode (InstructionNode node){
		m_inst = node.m_inst;
	}

	public Node clone() {
		return new InstructionNode(this);
	}

	public String toString(){
		return m_inst.toString();
	}
	
	public void display(int indentLevel) {
		printIndent(indentLevel);
		System.out.println(m_inst);
	}

	public Collection<Node> getContaintedNodes() {
		Collection<Node> containedNodes = new HashSet<Node> ();
		containedNodes.add(this);
		return containedNodes;
	}

	public String getGraphvizName() {
		return "Instruction" + m_id;
	}

	public List<XtaInstruction> getInstructions() {
		LinkedList<XtaInstruction> inst = new LinkedList<XtaInstruction>();
		inst.add(m_inst);
		return inst;
	}

	public long getNumberCycles() {
		return m_inst.getNumCycles();
	}

	public long getNumberFNOPS() {
		if(m_inst.isFnop()){
			return 1;
		} else {
			return 0;
		}
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

}
