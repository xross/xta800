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
	public TerminalNode(){
		m_terminals = new HashSet<Node>();
	}

	public Node deepClone() {
		TerminalNode tn = new TerminalNode();
		for(Node terminal : m_terminals){
			Node clone = terminal.clone();
			tn.m_terminals.add(clone);
		}
		return tn;
	}
	public double getTimeTaken() {
		return 0;
	}
	
	public long getNumCycles() {
		return 0;
	}

	public long getNumInstructions() {
		return getInstructions().size();
	}
	
	public long getNumFNops() {
		return 0;
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

	public List<XtaInstruction> getInstructions() {
		List<XtaInstruction> insts = new LinkedList<XtaInstruction>();
		for(Node nodes: m_terminals){
			insts.addAll(nodes.getInstructions());
		}
		return insts;
	}

	public Set<Integer> getPCsCovered() {
		return null;
	}

	public String getFunctionName() {
		return "";
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
		nodeTraverser.foundTerminalNode(this, depth);
		for (Node node : m_terminals) {
			node.traverse(depth + 1, nodeTraverser);			
		}		
	}

}
