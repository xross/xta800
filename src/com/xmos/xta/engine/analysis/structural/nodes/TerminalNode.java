package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
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
		double time = 0;
	    for (Node node : m_terminals) {
		    time += node.getTimeTaken(c);
		}
		return time;
	}
	
	public long getNumCycles(Case c) {
   		long cycles = 0;
    	for (Node node : m_terminals){
	    	cycles += node.getNumCycles(c);
		}
		return cycles;
	}

	public long getNumInstructions(Case c) {
		return getInstructions().size();
	}
	
	public long getNumFNops(Case c) {
		long fnops = 0;
   		for (Node node : m_terminals) {
    		fnops += node.getNumFNops(c);
		}
		return fnops;
	}
	
	public LinkedList<XtaInstruction> getInstructions() {
		LinkedList<XtaInstruction> insts = new LinkedList<XtaInstruction>();
		for(Node nodes: m_terminals) {
			insts.addAll(nodes.getInstructions());
		}
		return insts;
	}
	
	public Node clone() {
		return null;
	}

	public Collection<Node> getContainedNodes() {
		Collection<Node> containedNodes = new HashSet<Node> ();
		for(Node loop: m_terminals){
			containedNodes.addAll(loop.getContainedNodes());
		}
		return containedNodes;
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

	public List<Node> getChildren() {
		return new LinkedList<Node>(m_terminals);
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
	public List<XtaInstruction> getInstructionStream(Case c) {
		if(c == Case.WORST){
			return getWorstCaseOption().getInstructionStream(Case.WORST);
		} else if(c == Case.BEST){
			return getBestCaseOption().getInstructionStream(Case.BEST);
		} else {
			assert(false);
			return null;
		}
	}		
	
	private Node getWorstCaseOption() {
		Iterator<Node> iter = m_terminals.iterator();
		Node worstCaseNode = iter.next();
		while (iter.hasNext()) {
			Node node = iter.next();
			if (node.getTimeTaken(Case.WORST) > worstCaseNode.getTimeTaken(Case.WORST)) {
				worstCaseNode = node;
			}						
		}		
		return worstCaseNode;
	}
	
	private Node getBestCaseOption() {
		Iterator<Node> iter = m_terminals.iterator();
		Node worstCaseNode = iter.next();
		while (iter.hasNext()) {
			Node node = iter.next();
			if (node.getTimeTaken(Case.BEST) < worstCaseNode.getTimeTaken(Case.BEST)) {
				worstCaseNode = node;
			}						
		}		
		return worstCaseNode;
	}
	
}
