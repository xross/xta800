package com.xmos.xta.engine.analysis.nodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.xmos.xta.engine.instructions.XtaInstruction;

public class TerminalNode extends Node {
	Collection<Node> m_terminals;
	Node m_selectedTerminal;
	
	public TerminalNode(Collection<Node> terminals){
		m_terminals = new HashSet<Node>(terminals);
	}
	
	public Node clone() {
		return null;
	}

	public Collection<Node> getContaintedNodes() {
		Collection<Node> containedNodes = new HashSet<Node> ();
		for(Node loop: m_terminals){
			containedNodes.addAll(loop.getContaintedNodes());
		}
		return containedNodes;
	}

	public List<XtaInstruction> getInstructionStream() {
		// TODO Auto-generated method stub
		return new LinkedList<XtaInstruction>();
	}

	public List<XtaInstruction> getInstructions() {
		List<XtaInstruction> insts = new LinkedList<XtaInstruction>();
		for(Node nodes: m_terminals){
			insts.addAll(nodes.getInstructions());
		}
		return insts;
	}

	public long getNumberCycles() {
		return 0;
	}

	public long getNumberFNOPS() {
		return 0;
	}

	public Set<Integer> getPCsCovered() {
		return null;
	}

	public String getGraphvizName() {
		return "cluster_Terminal" + m_id;
	}

	public boolean isFunction() {
		return false;
	}
	
	public int getStartPc() {
		int pc = 0;
		for(Node n : m_terminals){
			pc = Math.max(pc, n.getStartPc());
		}
		return pc;
	}
	
	public void traverse(int depth, NodeTraverser nodeTraverser) {
	}
	
	public void display(int indentLevel) {
		printIndent(indentLevel);
		System.out.printf("terminal(%d)\n", m_id);
		for (Node node : m_terminals) {
			node.display(indentLevel + 1);
		}				
	}	
}
