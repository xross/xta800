package com.xmos.xta.engine.analysis.nodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.xmos.xta.engine.instructions.XtaInstruction;

public class BlockNode extends Node {
	private final static boolean DEBUG = false;
	
	private List<Node> m_insts;
	private List<XtaInstruction> m_xtaInsts;
	
	public BlockNode(BlockNode block){
		super();
		m_insts = block.m_insts;
	}
	
	public BlockNode(){
		super();
	}

	public Node deepClone() {
		return clone();
	}
	public BlockNode clone(){
		return new BlockNode(this);
	}
	
	public BlockNode(List<Node> insts){
		super();
		m_insts = insts;
	}
		
	public double getTimeTaken() {
		double timeTaken = 0;
		for (XtaInstruction inst : getInstructions()) {
			timeTaken += inst.getTimeTaken();
		}
        return timeTaken;		
	}

	public long getNumCycles() {
		long cycles = 0;
		for (XtaInstruction inst : getInstructions()) {
			cycles += inst.getNumCycles();
		}
		return cycles;
	}

	public long getNumInstructions() {
		return getInstructions().size();
	}
	
	public long getNumFNops() {
		return 0;
	}

	public List<XtaInstruction> getInstructions() {
		if (m_xtaInsts == null) {
			m_xtaInsts = new LinkedList<XtaInstruction>();
			for (Node i : m_insts) {
				m_xtaInsts.addAll(i.getInstructions());
			}
		}
		return m_xtaInsts;
	}
		
	public Set<Integer> getPCsCovered() {
		Set<Integer> pcs = new HashSet<Integer>();
		for (XtaInstruction inst : getInstructions()) {
			pcs.add(inst.getPc());
		}
		return pcs;
	}

	public String makeGraphVisLabels() {
		return "Node" + m_id + "[label=\"Node "+ m_id + "\"];\n";
	}

	public String makeGraphVisLinks() {
		StringBuffer sb = new StringBuffer();
		for (Node next : getNextNodes()) {
			sb.append("Node" + m_id + " -> Node" + next.m_id+";\n");
		}
		return sb.toString();
	}

	public Collection<Node> getContaintedNodes() {
		return new HashSet<Node> (m_insts);
	}

	public String getFunctionName() {
		return "";
	}
	
	public String getGraphvizName() {
		return "Block" + m_id;
	}
	
	public boolean isFunction() {
		return false;
	}

	public int getStartPc() {
		return getInstructions().get(0).getPc();
	}	
	
	public void traverse(int depth, NodeTraverser nodeTraverser) {
		nodeTraverser.foundBlockNode(this, depth);
		if (DEBUG) { 
			for (Node instruction : m_insts) {
				instruction.traverse(depth + 1, nodeTraverser);
			}
		}		
	}

	
}
