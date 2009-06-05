package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.xmos.xta.engine.instructions.XtaInstruction;
import com.xmos.xta.engine.references.types.ReferenceType;

public class TerminalNode extends Node {
	private Collection<Node> m_terminals;
	
	public TerminalNode(Collection<Node> terminals){
		m_terminals = new HashSet<Node>(terminals);
	}
	public TerminalNode(){
		m_terminals = new HashSet<Node>();
	}

	public Node deepClone(Map<Integer, FunctionNode> startPcs) {
		TerminalNode tn = new TerminalNode();
		for (Node terminal : m_terminals) {
			Node clone = terminal.deepClone(startPcs);
			tn.m_terminals.add(clone);
		}
		return tn;
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

	public Set<Integer> getPCs() {
		Set<Integer> pcs = new HashSet<Integer>();
		for(Node node : m_terminals){
			pcs.addAll(node.getPCs());
		}
		return pcs;
	}

	public String getFunctionName() {
		return "";
	}

	public int getStartPc() {
		int pc = 0;
		for(Node n : m_terminals){
			pc = Math.max(pc, n.getStartPc());
		}
		return pc;
	}
	
	public void traverse(int depth, NodeTraverser nodeTraverser) {
		if (!nodeTraverser.foundTerminalNode(this, depth))
			return;
		
		for (Node node : m_terminals) {
			node.traverse(depth + 1, nodeTraverser);			
		}		
	}
	
	public String toString(){
		return "Terminal" + m_id;
	}

	public Collection<Node> getChildern() {
		return m_terminals;
	}

	public Collection<Node> whatsNext(Node node) {
		return getParent().whatsNext(this);
	}

	boolean checkBackTrail(Stack<ReferenceType> referenceTypes, Node node, 
			boolean calledFromAbove, FunctionCallStatus functionCallStatus) {
		
		if (!calledFromAbove) {
		    return super.checkBackTrail(referenceTypes, node, calledFromAbove, functionCallStatus);
		}
		return false;
	}		
}
