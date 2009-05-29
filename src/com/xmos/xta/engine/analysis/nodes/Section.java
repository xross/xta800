package com.xmos.xta.engine.analysis.nodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.xmos.xta.engine.instructions.XtaInstruction;

public class Section extends Node {
	
	//each option denotes a different path in the section - all weighted equally
	private Collection<Node> m_options;
	private Node m_jumpNode;
	private Node m_selectedOption; 

	public Section(Collection<Node> options, Node jumpNode) {
		super();
		m_options = options;
		m_selectedOption = null;
		m_jumpNode = jumpNode;
	}
	
	public Section(Section ms) {
		super();
		m_options = ms.m_options;
		m_selectedOption = ms.m_selectedOption;
		m_jumpNode = ms.m_jumpNode;
	}
	
	
	public Collection<Node> getOptions(){
		return m_options;
	}
	
	public Node getJumpNode(){
		return m_jumpNode;
	}
	
	public void setSelectedOption(Node selected){
		assert(m_options.contains(selected));
		m_selectedOption = selected;
	}

	public List<XtaInstruction> getInstructions() {
		if(m_selectedOption!= null){
			return m_selectedOption.getInstructions();
		} else {
			return new LinkedList<XtaInstruction>();
		}
	}

	public long getNumberCycles() {
		if(m_selectedOption!= null){
			return m_selectedOption.getNumberCycles();
		} else {
			return 0;
		}
	}

	public long getNumberFNOPS() {
		if(m_selectedOption!= null){
			return m_selectedOption.getNumberFNOPS();
		} else {
			return 0;
		}
	}

	public Set<Integer> getPCsCovered() {
		if(m_selectedOption!= null){
			return m_selectedOption.getPCsCovered();
		} else {
			return new HashSet<Integer>();
		}
	}

	public Node clone() {
		return new Section(this);
	}

	public Collection<Node> getContaintedNodes() {
		Collection<Node> containedNodes = new HashSet<Node> ();
		for(Node loop: m_options){
			containedNodes.addAll(loop.getContaintedNodes());
		}
		return containedNodes;
	}

	//Collection <Node> m_nextNodes = new HashSet<Node>();

	public String getGraphvizName() {
		return "cluster_Section" + m_id;
	}
	
	public void display(int indentLevel) {
		printIndent(indentLevel);
		System.out.printf("multisection(%d)\n", m_id);
		
		for (Node node : m_options) {
			printIndent(indentLevel);
			System.out.printf(" option:\n");
			node.display(indentLevel + 1);
		}		
	}	
	public boolean isFunction() {
		return false;
	}

	public int getStartPc() {
		int pc = 0;
		for(Node n : m_options){
			pc = Math.max(pc, n.getStartPc());
		}
		return pc;
	}
}
