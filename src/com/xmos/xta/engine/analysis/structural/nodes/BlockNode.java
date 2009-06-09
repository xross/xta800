package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.engine.analysis.structural.nodes.Node.IBufferSequence;
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
	
	public BlockNode shallowCopy() {
		return new BlockNode(this);
	}
	
	public Node deepCopy(Map<Integer, FunctionNode> startPcs) {
		BlockNode bn = new BlockNode();
		for (Node node : m_insts) {
			bn.m_insts.add(node.deepCopy(startPcs));
		}
		return bn;
	}
	
	public double getTimeTakenRelative(Case c) {
		double time = 0;
		for (Node node : m_insts) {
			time += node.getTimeTakenRelative(c);
		}
        return time;		
	}

	public double getTimeTakenAbsolute(Case c) {
		double time = 0;
		for (Node node : m_insts) {
			time += node.getTimeTakenAbsolute(c);
		}
        return time;		
	}
	
	public long getNumCyclesRelative(Case c) {
		long count = 0;
		for (Node node : m_insts) {
			count += node.getNumCyclesRelative(c);
		}
		return count;
	}

	public long getNumCyclesAbsolute(Case c) {
		long count = 0;
		for (Node node : m_insts) {
			count += node.getNumCyclesAbsolute(c);
		}
		return count;
	}
	
	public long getNumInstructionsRelative(Case c) {
		long count = 0;
		for (Node node : m_insts) {
			count += node.getNumInstructionsRelative(c);
		}
		return count;
	}

	public long getNumInstructionsAbsolute(Case c) {
		long count = 0;
		for (Node node : m_insts) {
			count += node.getNumInstructionsAbsolute(c);
		}
		return count;
	}
	
	public long getNumFNopsRelative(Case c) {
		long count = 0;
		for (Node node : m_insts) {
			count += node.getNumFNopsRelative(c);
		}
		return count;
	}

	public long getNumFNopsAbsolute(Case c) {
		long count = 0;
		for (Node node : m_insts) {
			count += node.getNumFNopsAbsolute(c);
		}
		return count;
	}
	
	public LinkedList<XtaInstruction> getInstructions() {
		LinkedList<XtaInstruction> insts = new LinkedList<XtaInstruction>();
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
		return getInstructions().getFirst().getPc();
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

	public List<XtaInstruction> getInstructionStream(Case c) {
		List<XtaInstruction> insts = new LinkedList<XtaInstruction>();
		for(Node inst : m_insts){
			insts.addAll(inst.getInstructions());
		}
		return insts;
	}

	public void getInstructionStream(Case c, Integer ibuffer, List<XtaInstruction> instructions) {
		
		IBufferSequence ibufferSeq = m_ibufferStates.get(ibuffer);
		List<Integer> fnops = ibufferSeq.m_fnopPcs;
		for(Node inst : m_insts){
			instructions.addAll(inst.getInstructions());
			if(fnops.contains(inst.getStartPc())){
				//add an fnop or two
				
			}
		}
	}
	
}
