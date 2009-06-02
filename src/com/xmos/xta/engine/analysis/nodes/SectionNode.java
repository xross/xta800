package com.xmos.xta.engine.analysis.nodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.xmos.xta.engine.instructions.XtaInstruction;

public class SectionNode extends Node {
	
	//each option denotes a different path in the section - all weighted equally
	private Collection<Node> m_options;
	private Node m_jumpNode;
	private Node m_selectedOption; 

	public SectionNode(Collection<Node> options, Node jumpNode) {
		super();
		m_options = options;
		m_selectedOption = null;
		m_jumpNode = jumpNode;
	}
	
	public SectionNode(SectionNode ms) {
		super();
		m_options = ms.m_options;
		m_selectedOption = ms.m_selectedOption;
		m_jumpNode = ms.m_jumpNode;
	}

	public double getTimeTaken() {
		if (m_selectedOption!= null) {
			return m_selectedOption.getTimeTaken();
		} else {
			return 0;
		}
	}

	public long getNumCycles() {
		if (m_selectedOption!= null) {
			return m_selectedOption.getNumCycles();
		} else {
			return 0;
		}
	}

	public long getNumInstructions() {
		return getInstructions().size();
	}
	
	public long getNumFNops() {
		if (m_selectedOption!= null) {
			return m_selectedOption.getNumFNops();
		} else {
			return 0;
		}
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
		if (m_selectedOption!= null) {
			return m_selectedOption.getInstructions();
		} else {
			return new LinkedList<XtaInstruction>();
		}
	}

	public Set<Integer> getPCsCovered() {
		if(m_selectedOption!= null) {
			return m_selectedOption.getPCsCovered();
		} else {
			return new HashSet<Integer>();
		}
	}

	public Node clone() {
		return new SectionNode(this);
	}

	public Collection<Node> getContaintedNodes() {
		Collection<Node> containedNodes = new HashSet<Node> ();
		for(Node loop: m_options){
			containedNodes.addAll(loop.getContaintedNodes());
		}
		return containedNodes;
	}

	public String getFunctionName() {
		return "";
	}
	
	public String getGraphvizName() {
		return "cluster_Section" + m_id;
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

	public void traverse(int depth, NodeTraverser nodeTraverser) {
		nodeTraverser.foundSectionNode(this, depth);
		for (Node node : m_options) {
			node.traverse(depth + 1, nodeTraverser);			
		}		
	}	
}
