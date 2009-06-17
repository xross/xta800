package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.xmos.xta.engine.analysis.controlflow.InstructionBuffer;
import com.xmos.xta.engine.analysis.structural.StructureFnoper;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.BlockNodeAttributes;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.engine.instructions.NullInstruction;
import com.xmos.xta.engine.instructions.XtaInstruction;

public class BlockNode extends Node {


	private BlockNodeAttributes m_attributes;	

	private List<InstructionNode> m_insts;
	private List<Integer> m_worstCaseFnops;
	private List<Integer> m_bestCaseFnops;
	
	public BlockNode() {
		super();
		m_worstCaseFnops = new LinkedList<Integer>();
		m_bestCaseFnops = new LinkedList<Integer>();
		m_insts = new LinkedList<InstructionNode>();
        m_attributes = new BlockNodeAttributes(this);
	}
	
	public BlockNode(BlockNode block) {
		//super(block);
		m_insts = block.m_insts;
		m_attributes = new BlockNodeAttributes(this);
	}
	
	public BlockNode(List<Node> insts) {
		super();
		m_insts = new LinkedList<InstructionNode>();
		for(Node node : insts){
			assert(node instanceof InstructionNode);
			m_insts.add((InstructionNode)node);
		}
        m_attributes = new BlockNodeAttributes(this);
	}
		
	public BlockNode shallowCopy() {
		return new BlockNode(this);
	}

	public Node deepCopy(Map<Integer, FunctionNode> startPcs) {		
		BlockNode bn = new BlockNode();
		for (Node node : m_insts) {
			bn.m_insts.add((InstructionNode)node.deepCopy(startPcs));
		}
		bn.m_attributes = new BlockNodeAttributes(bn);
		return bn;
	}

    public String getTypeName() {
        return "block";
    }
    
    public NodeAttributes getAttributes() {
    	return m_attributes;
    }
    
	public boolean isBlockNode() {
		return true;
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
		LinkedList<Node> children = new LinkedList<Node>();
		for(InstructionNode node : m_insts){
			children.add(node);
		}
		return children;
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


/*
	private double getFNOPTimeTaken(InstructionNode instNode) {
		XtaInstruction inst = instNode.getXtaInstruction();
		XtaInstruction fnop = new NullInstruction("--FNOP--", inst.getConfig(), 0, inst.getPc(), 0);
    	int threadFactor = (inst.getConfig().m_numThreads > 4) ? inst.getConfig().m_numThreads : 4;
        return ((double)(threadFactor * 1000) / (double)inst.getConfig().m_frequency) * fnop.getNumCycles();
	}
*/

	
	public void getInstructionStream(Case c, List<InstructionNode> instructions, long iteration) {
		for(InstructionNode inst : m_insts){
			
			List<Integer> fnops = null;
			switch (c){
			case BEST:
				fnops = m_bestCaseFnops;
				break;
			case WORST:
				fnops = m_worstCaseFnops;
				break;
			}
			if(fnops!= null){
			XtaInstruction xtaInst = inst.getXtaInstruction();
			for(int pc : fnops){
				
				if(xtaInst.getPc() == pc){
					XtaInstruction fnop = new NullInstruction("--FNOP--", xtaInst.getConfig(), 0, xtaInst.getPc(), 0);
					InstructionNode fnopNode = new InstructionNode(fnop);
					instructions.add(fnopNode);
				}
			}
			}
			instructions.add(inst);
		}

	}

	public long getError(Case c) {
		return m_worstCaseFnops.size() - m_bestCaseFnops.size();
	}

	public void addFnops() {
		int worstCount = -1;
		InstructionBuffer worstInstructionBuffer = null;
		for(Node prevBlock : whatsBefore(this)){
			for(InstructionBuffer ib : prevBlock.getIbufferDueToWorstTime()){
				InstructionBuffer copy = new InstructionBuffer(ib);
				List<Integer> fnopList = StructureFnoper.makeFnops(prevBlock, copy, getParent());
				if(fnopList.size() > worstCount){
					worstInstructionBuffer = copy;
					worstCount = fnopList.size();
					m_worstCaseFnops = fnopList;
					
				}
			}
		}
		addEndIbufferDueToWorst(worstInstructionBuffer);

		worstCount = -1;
		InstructionBuffer bestInstructionBuffer = null;
		for(Node prevBlock : whatsBefore(this)){
			for(InstructionBuffer ib : prevBlock.getIbufferDueToBestTime()){
				InstructionBuffer copy = new InstructionBuffer(ib);
				List<Integer> fnopList = StructureFnoper.makeFnops(prevBlock, copy, getParent());
				if(fnopList.size() > worstCount){
					bestInstructionBuffer = copy;
					worstCount = fnopList.size();
					m_bestCaseFnops = fnopList;
				}
			}
		}
		addEndIbufferDueToBest(bestInstructionBuffer);

		
		/*
		
		List<InstructionNode> insts = new LinkedList<InstructionNode>();
		getInstructionStream(Case.WORST, insts , 0);
		
		for(InstructionNode inst : insts){
			System.out.println(inst);
		}
		System.out.println();
		*/
	}

	public Collection<Node> whatsBefore(Node node) {
		if(getParent()!= null){
			return getParent().whatsBefore(node);
		}
		return new HashSet<Node>();
	}

	public List<Node> getImmediateChildren() {
		return new LinkedList<Node>(m_insts.subList(0, 1));
	}


	

}
