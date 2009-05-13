package com.xmos.xta.engine.xtagraph.nodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.xmos.xta.engine.XtaInstruction;

public class Terminal extends Node {

	Collection<Node> m_terminals;
	Node m_selectedTerminal;
	
	public Terminal(Collection<Node> terminals){
		m_terminals = new HashSet<Node>(terminals);
	}
	
	@Override
	public Node clone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Node> getContaintedNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<XtaInstruction> getInstructionStream() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<XtaInstruction> getInstructions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getNumberCycles() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getNumberFNOPS() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Set<Integer> getPCsCovered() {
		// TODO Auto-generated method stub
		return null;
	}

	public void appendToGraph(StringBuffer body, StringBuffer connections,
			Collection<Node> last, Collection<Node> next, int indent, Node parent) {
		body.append(intToTab(indent) +"subgraph " + getGraphvizName()  + " {\n");
		body.append(intToTab(indent) +"style=filled;\n");
		body.append(intToTab(indent) +"fillcolor=lavender;\n");
		body.append(intToTab(indent) +"color=black;\n");
		
		for(Node terminal : m_terminals){
			Collection<Node> nextThis = new HashSet<Node>();
			terminal.appendToGraph(body, connections, last, nextThis, indent+1, this);
		}
		body.append(intToTab(indent) +"}\n");
		
		return;
		
	}

	public String getGraphvizName() {
		return "cluster_Terminal" + m_id;
	}

	public void display(int indentLevel) {
		printIndent(indentLevel);
		System.out.printf("terminal(%d)\n", m_id);
		for (Node node : m_terminals) {
			node.display(indentLevel + 1);
		}				
	}			
}
