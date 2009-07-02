package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.TreeSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.BlockNodeAttributes;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.engine.instructions.NullInstruction;
import com.xmos.xta.engine.instructions.XtaInstruction;

public class BlockNode extends IBufferNode {
	private BlockNodeAttributes m_attributes;	
	private List<InstructionNode> m_insts;
	
	public BlockNode(BlockNode block) {
		m_insts = block.m_insts;
		m_attributes = new BlockNodeAttributes(this);
	}
	
	public BlockNode(List<InstructionNode> insts) {
		super();
		m_insts = insts;
        m_attributes = new BlockNodeAttributes(this);
	}
		
	public BlockNode shallowCopy() {
		return new BlockNode(this);
	}

	public Node deepCopy(Map<Integer, FunctionNode> startPcs, Engine engine) {		
		return new BlockNode(m_insts);
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
		for (Node inst : m_insts){
			insts.addAll(inst.getInstructions());
		}
		return insts;
	}
			
	public Set<Integer> getPCs() {
		Set<Integer> pcs = new TreeSet<Integer>();
		for (XtaInstruction inst : getInstructions()) {
			pcs.add(inst.getPc());
		}
		return pcs;
	}

	public Collection<Node> getContainedNodes() {
		return new TreeSet<Node> (m_insts);
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
		return new LinkedList<Node>(m_insts);
	}

	public Collection<Node> whatsNext(Node node) {
		int index = m_insts.indexOf(node);
		if(index < m_insts.size()-1){
			Collection<Node> next = new TreeSet<Node>();
			next.add(m_insts.get(index+1));
			return next;
		} else {
			if (getParent() != null){
				return getParent().whatsNext(this);
			}
			return new TreeSet<Node>();
		}
	}
	
	public void getInstructionStream(Case c, List<InstructionNode> instructions, long iteration) {
		for(InstructionNode inst : m_insts){
			instructions.add(inst);
		}

	}
	
	public Collection<Node> whatsBefore(Node node) {
		if(getParent()!= null){
			return getParent().whatsBefore(node);
		}
		return new TreeSet<Node>();
	}

	public List<Node> getImmediateChildren() {
		return new LinkedList<Node>(m_insts.subList(0, 1));
	}

	public void insertFnops() {
		List<InstructionNode> insts = new LinkedList<InstructionNode>();
		for(InstructionNode inst : m_insts){
			List<Integer> fnops =  m_worstIbufferSequence.getfnops();
			XtaInstruction xtaInst = inst.getXtaInstruction();
			for(int pc : fnops){
				if(xtaInst.getPc() == pc){
					XtaInstruction fnop = new NullInstruction("--FNOP--", xtaInst.getConfig(), 0, xtaInst.getPc(), 0);
					InstructionNode fnopNode = new InstructionNode(fnop);
					insts.add(fnopNode);
				}
			}
			insts.add(inst);
		}
		m_insts = insts;
	}

}
