package com.xmos.xta.engine.xtagraph.nodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.xmos.xta.engine.XtaInstruction;

public class MultiSection extends Node {
	
	//each option denotes a different path in the section - all weighted equally
	private Collection<Node> m_options;
	private Node m_jumpNode;
	private Node m_selectedOption; 

	public MultiSection(Collection<Node> options, Node jumpNode) {
		super();
		m_options = options;
		m_selectedOption = null;
		m_jumpNode = jumpNode;
	}
	
	public MultiSection(MultiSection ms) {
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
	
	public List<XtaInstruction> getInstructionStream() {
		if(m_selectedOption!= null){
			return m_selectedOption.getInstructionStream();
		} else {
			return new LinkedList<XtaInstruction>();
		}
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
		return new MultiSection(this);
	}

	public Collection<Node> getContaintedNodes() {
		Collection<Node> containedNodes = new HashSet<Node> ();
		for(Node loop: m_options){
			containedNodes.addAll(loop.getContaintedNodes());
		}
		return containedNodes;
	}

	Collection <Node> m_nextNodes = new HashSet<Node>();
	
	public void appendToGraph(StringBuffer body, StringBuffer connections,
			Collection<Node> last, Collection<Node> next, int indent, Node parent) {
		
		body.append(intToTab(indent) +"subgraph " + getGraphvizName()  + " {\n");
		body.append(intToTab(indent) +"style=filled;\n");
		body.append(intToTab(indent) +"fillcolor=lightblue;\n");
		body.append(intToTab(indent) +"color=black;\n");
		for (Node option : m_options) {
			Collection<Node> nextThis = new HashSet<Node>();
			option.appendToGraph(body, connections, last, nextThis, indent+1, this);
			next.addAll(nextThis);
		}
		body.append(intToTab(indent) +"}\n");
		
		if (m_jumpNode != null)
			next.addAll(last);
		
		return;
	}

	public String getGraphvizName() {
		return "cluster_Section" + m_id;
	}
	
	public void display(int indentLevel) {
		printIndent(indentLevel);
		System.out.printf("multisection(%d)\n", m_id);
		
		for (Node node : m_options) {
			node.display(indentLevel + 1);
		}		
	}			
}
