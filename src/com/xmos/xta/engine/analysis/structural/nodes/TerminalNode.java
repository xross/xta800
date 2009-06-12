package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.engine.instructions.XtaInstruction;
import com.xmos.xta.engine.references.types.ReferenceType;

public class TerminalNode extends Node {
	private Collection<Node> m_terminals;
	
	public TerminalNode(Collection<Node> terminals) {
		m_terminals = new HashSet<Node>(terminals);
	}
	
	public TerminalNode() {
		m_terminals = new HashSet<Node>();
	}

	public Node shallowCopy() {
		return null;
	}
	
	public Node deepCopy(Map<Integer, FunctionNode> startPcs) {
		TerminalNode tn = new TerminalNode();
		for (Node terminal : m_terminals) {
			Node clone = terminal.deepCopy(startPcs);
			tn.m_terminals.add(clone);
		}
		return tn;
    }
		
    public String getTypeName() {
        return "terminal";
    }
		
	public LinkedList<XtaInstruction> getInstructions() {
		LinkedList<XtaInstruction> insts = new LinkedList<XtaInstruction>();
		for(Node nodes: m_terminals) {
			insts.addAll(nodes.getInstructions());
		}
		return insts;
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

	// --------------------------------------------------------------------- //
	// Statistics Gathering Methods
	// --------------------------------------------------------------------- //
	public double getTimeTakenRelative(Case c) {
		double time = 0;
	    for (Node node : m_terminals) {
		    time += node.getTimeTakenRelative(c);
		}
		return time;
	}

	public double getTimeTakenAbsolute(Case c) {
		double time = 0;
	    for (Node node : m_terminals) {
		    time += node.getTimeTakenAbsolute(c);
		}
		return time;
	}
	
	public long getNumCyclesRelative(Case c) {
   		long count = 0;
    	for (Node node : m_terminals){
    		count += node.getNumCyclesRelative(c);
		}
		return count;
	}

	public long getNumCyclesAbsolute(Case c) {
   		long count = 0;
    	for (Node node : m_terminals){
    		count += node.getNumCyclesAbsolute(c);
		}
		return count;
	}
	
	public long getNumInstructionsRelative(Case c) {
   		long count = 0;
    	for (Node node : m_terminals){
    		count += node.getNumInstructionsRelative(c);
		}
		return count;
	}

	public long getNumInstructionsAbsolute(Case c) {
   		long count = 0;
    	for (Node node : m_terminals){
    		count += node.getNumInstructionsAbsolute(c);
		}
		return count;
	}
	
	public long getNumFNopsRelative(Case c) {
   		long count = 0;
    	for (Node node : m_terminals){
    		count += node.getNumFNopsRelative(c);
		}
		return count;
	}

	public long getNumFNopsAbsolute(Case c) {
   		long count = 0;
    	for (Node node : m_terminals){
    		count += node.getNumFNopsAbsolute(c);
		}
		return count;
	}

	public void getInstructionStream(Case c, List<XtaInstruction> instructions) {
		
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

/*
	public void updateFnops(Case c, IBufferVector ibv) {
		Map<Node, IBufferVector> nodeToIBuff = new HashMap<Node, IBufferVector>();
		for(Node option : m_terminals){
			option.updateFnops(c, ibv);
			nodeToIBuff.put(option, option.getIBufferVector(c));
		}
		if(c == Case.WORST){
			m_worst = maximiseWorst(c, nodeToIBuff);
			m_worstCaseEndIbuffer = nodeToIBuff.get(m_worst);
		} else if(c == Case.BEST){
			m_best = maximiseBest(c, nodeToIBuff);
			m_bestCaseEndIbuffer = nodeToIBuff.get(m_best);
		} else {
			assert(false);
		}
	}
*/
	public void updateFnops(IBufferVector bestVector, IBufferVector worstVector) {
		m_bestCaseStartIbuffer = bestVector;
		m_worstCaseStartIbuffer = worstVector;
		
		
	}	


	
}
