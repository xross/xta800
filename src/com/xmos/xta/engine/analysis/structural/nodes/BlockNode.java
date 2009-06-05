package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.xmos.xta.engine.instructions.XtaInstruction;

public class BlockNode extends Node {
	private List<Node> m_insts;

	public BlockNode() {
		super();
		m_insts = new LinkedList<Node>();
	}
	
	public BlockNode(BlockNode block) {
		super();
		m_insts = block.m_insts;
	}
	
	public BlockNode(List<Node> insts) {
		super();
		m_insts = insts;
	}
	
	public Node deepClone(Map<Integer, FunctionNode> startPcs) {
		BlockNode bn = new BlockNode();
		for (Node node : m_insts) {
			bn.m_insts.add(node.deepClone(startPcs));
		}
		return bn;
	}
	
	public BlockNode clone() {
		return new BlockNode(this);
	}

	public double getTimeTaken(Case c) {
		double timeTaken = 0;
		for (XtaInstruction inst : getInstructions()) {
			timeTaken += inst.getTimeTaken();
		}
        return timeTaken;		
	}

	public long getNumCycles(Case c) {
		long cycles = 0;
		for (XtaInstruction inst : getInstructions()) {
			cycles += inst.getNumCycles();
		}
		return cycles;
	}

	public long getNumInstructions(Case c) {
		return getInstructions().size();
	}

	public long getNumFNops(Case c) {
		return 0;
	}

	public List<XtaInstruction> getInstructions() {
		List<XtaInstruction> insts = new LinkedList<XtaInstruction>();
		for(Node inst : m_insts){
			insts.addAll(inst.getInstructions());
		}
		return insts;
	}
		
	public Set<Integer> getPCs() {
		Set<Integer> pcs = new HashSet<Integer>();
		for (XtaInstruction inst : getInstructions()) {
			pcs.add(inst.getPc());
		}
		return pcs;
	}

	public Collection<Node> getContainedNodes() {
		return new HashSet<Node> (m_insts);
	}

	public String getFunctionName() {
		return "";
	}
	
	public int getStartPc() {
		return getInstructions().get(0).getPc();
	}	
	
	public void traverse(int depth, NodeTraverser nodeTraverser) {
		if (!nodeTraverser.foundBlockNode(this, depth)) 
			return;
			
		for (Node instruction : m_insts) {
			instruction.traverse(depth + 1, nodeTraverser);
		}		
	}
	
	public String toString(){
		return "Block" + m_id;
	}

	public List<Node> getChildren() {
		return m_insts;
	}

	public Collection<Node> whatsNext(Node node) {
		int index = m_insts.indexOf(node);
		if(index < m_insts.size()-1){
			Collection<Node> next = new HashSet<Node>();
			next.add(m_insts.get(index+1));
			return next;
		} else {
			return getParent().whatsNext(this);
		}
	}
}
