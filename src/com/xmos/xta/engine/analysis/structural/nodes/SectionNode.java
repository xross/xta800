package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.xmos.xta.engine.instructions.XtaInstruction;

public class SectionNode extends Node {
	private Collection<Node> m_options;
	private Node m_jumpNode;

	public SectionNode(Collection<Node> options, Node jumpNode) {
		super();
		m_options = options;
		m_jumpNode = jumpNode;
	}
	
	public SectionNode(SectionNode ms) {
		super();
		m_options = ms.m_options;
		m_jumpNode = ms.m_jumpNode;
	}

	public SectionNode() {
		super();
		m_options = new HashSet<Node>();
		m_jumpNode = null;
	}

	public Node deepClone(Map<Integer, FunctionNode> startPcs) {
		SectionNode sn = new SectionNode();
		for(Node section : m_options){
			Node clone = section.deepClone(startPcs);
			sn.m_options.add(clone);
		}
		if(m_jumpNode != null){
			sn.m_jumpNode = m_jumpNode.deepClone(startPcs);	
		}
	
		return sn;
	}

	public double getTimeTaken(Case c) {
		return 0;
	}

	public long getNumCycles(Case c) {
		return 0;
	}

	public long getNumInstructions(Case c) {
		return getInstructions().size();
	}
	
	public long getNumFNops(Case c) {
		return 0;
	}
	
	public Collection<Node> getOptions(){
		return m_options;
	}
	
	public Node getJumpNode(){
		return m_jumpNode;
	}
	

	public List<XtaInstruction> getInstructions() {
		return new LinkedList<XtaInstruction>();
	}

	public Set<Integer> getPCs() {
		Set<Integer> pcs = new HashSet<Integer>();
		for(Node node : m_options){
			pcs.addAll(node.getPCs());
		}
		return pcs;
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
		if (!nodeTraverser.foundSectionNode(this, depth))
			return;
		
		for (Node node : m_options) {
			node.traverse(depth + 1, nodeTraverser);			
		}		
	}
	public String toString(){
		return "Section" + m_id;
	}

	public Collection<Node> getChildern() {
		return m_options;
	}

	public Collection<Node> whatsNext(Node node) {
		return getParent().whatsNext(this);
	}
}
