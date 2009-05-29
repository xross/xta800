package com.xmos.xta.engine.analysis.nodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.xmos.xta.engine.instructions.XtaInstruction;

public class Block extends Node {

	private List<Node> m_insts;
	
	public Block(Block block){
		super();
		m_insts = block.m_insts;
	}
	
	//this is only used for verification
	public Block(){
		super();
	}
	
	public Block clone(){
		return new Block(this);
	}
	
	public Block(List<Node> insts){
		super();
		m_insts = insts;
	}

	public int getBasicBlockId() {
		return m_id;
	}
	
	private List<XtaInstruction> m_xtaInsts ;
	
	public List<XtaInstruction> getInstructions() {
		if(m_xtaInsts == null){
			m_xtaInsts = new LinkedList<XtaInstruction>();
			for(Node i : m_insts){
				m_xtaInsts.addAll(i.getInstructions());
			}
		}
		return m_xtaInsts;
	}

	public long getNumberCycles() {
		long cycles = 0;
		for(XtaInstruction inst : getInstructions()){
			cycles += inst.getNumCycles();
		}
		return cycles;
	}

	public long getNumberFNOPS() {
		return 0;
	}

	public long getNumberInstructions() {
		return getInstructions().size();
	}

	public Set<Integer> getPCsCovered() {
		Set<Integer> pcs = new HashSet<Integer>();
		for(XtaInstruction inst : getInstructions()){
			pcs.add(inst.getPc());
		}
		return pcs;
	}

	public String makeGraphVisLabels() {
		return "Node" + m_id + "[label=\"Node "+ m_id + "\"];\n";
	}

	public String makeGraphVisLinks() {
		StringBuffer sb = new StringBuffer();
		for(Node next : getNextNodes()){
			sb.append("Node" + m_id + " -> Node" + next.m_id+";\n");
		}
		return sb.toString();
	}

	public Collection<Node> getContaintedNodes() {
		return new HashSet<Node> (m_insts);
	}

	public String getGraphvizName() {
		return "Block" + m_id;
	}

	private final static boolean inst = false;
	
	public void display(int indentLevel) {
		printIndent(indentLevel);
		System.out.println("block(" + m_id +")");
		if(inst){ 
			for (Node instruction : m_insts) {
				instruction.display(indentLevel+1);
			}
		}
	}

	public boolean isFunction() {
		return false;
	}

	public int getStartPc() {
		return getInstructions().get(0).getPc();
	}		

}
