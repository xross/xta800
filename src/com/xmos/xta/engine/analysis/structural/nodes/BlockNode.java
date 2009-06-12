package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.engine.instructions.NullInstruction;
import com.xmos.xta.engine.instructions.XtaInstruction;

public class BlockNode extends IbufferNode {
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
		bn.m_startToEndIbuffer = m_startToEndIbuffer;
		for (Node node : m_insts) {
			bn.m_insts.add(node.deepCopy(startPcs));
		}
		return bn;
	}

    public String getTypeName() {
        return "block";
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

	// --------------------------------------------------------------------- //
	// Statistics Gathering Methods
	// --------------------------------------------------------------------- //
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
		return 0;
	}

	public long getNumFNopsAbsolute(Case c) {
		return 0;
	}

	public void getInstructionStream(Case c, List<XtaInstruction> instructions) {
		for(XtaInstruction inst : getInstructions()){
			for(int i=0;i<fnopCount(inst.getPc());i++){
				instructions.add(new NullInstruction("--FNOP--", inst.getConfig(), 0, inst.getPc(), 0));
			}
			instructions.add(inst);
		}
	}
	/*
	public void updateFnops(IBufferVector bestVector, IBufferVector worstVector) {
		m_bestCaseStartIbuffer = bestVector;
		m_worstCaseStartIbuffer = worstVector;
		
		IBufferVector endVector = new IBufferVector();
		int byteNum = 0;
		for(int startNumBytesCount : m_worstCaseStartIbuffer.getVectors()){
			if(startNumBytesCount > 0){
				endVector.add(iBufferTransition(byteNum), startNumBytesCount);
			}
			byteNum++;
		}
		m_worstCaseEndIbuffer = endVector;
		
		endVector = new IBufferVector();
		byteNum = 0;
		for(int startNumBytesCount : m_bestCaseStartIbuffer.getVectors()){
			if(startNumBytesCount > 0){
				endVector.add(iBufferTransition(byteNum), startNumBytesCount);
			}
			byteNum++;
		}
		m_bestCaseEndIbuffer = endVector;
	}

	//this function takes an ibuffer fullness and returns the corresponding fnops
	private List<Integer> iBufferToFnops(int numBytes){
		return null;
	}
	*/
}
