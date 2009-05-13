package com.xmos.xta.engine.xtagraph.nodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.xmos.xta.engine.BasicBlock;
import com.xmos.xta.engine.XtaInstruction;

public class Block extends Node {

	private BasicBlock m_block;
	private List<XtaInstruction> m_fnops; 
	private List<XtaInstruction> m_instructionStream;
	
	public Block(Block block){
		super();
		m_block = block.m_block;
		m_instructionStream = block.m_instructionStream;
	}
	
	//this is only used for verification
	public Block(){
		super();
	}
	
	public Block clone(){
		return new Block(this);
	}
	
	public Block(BasicBlock block){
		super();
		m_block = block;
		m_instructionStream = new LinkedList<XtaInstruction>();
	}
	
	public List<XtaInstruction> getInstructionStream() {
		return m_instructionStream;
	}

	public int getBasicBlockId() {
		return m_block.getId();
	}
	
	public List<XtaInstruction> getInstructions() {
		return m_block.getInstructions();
	}

	public long getNumberCycles() {
		long cycles = 0;
		for(XtaInstruction inst : getInstructionStream()){
			cycles += inst.getNumCycles();
		}
		return cycles;
	}

	public long getNumberFNOPS() {
		return m_fnops.size();
	}

	public long getNumberInstructions() {
		return m_block.getInstructions().size() + m_fnops.size();
	}

	public Set<Integer> getPCsCovered() {
		Set<Integer> pcs = new HashSet<Integer>();
		for(XtaInstruction inst : m_block.getInstructions()){
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
		Collection<Node> containedNodes = new HashSet<Node> ();
		containedNodes.add(this);
		return containedNodes;
	}


	public void appendToGraph(StringBuffer body, StringBuffer connections,
			Collection<Node> last, Collection<Node> next, int indent, Node parent) {
		
		if(m_block != null){
			body.append(intToTab(indent) + getGraphvizName() + " [label = \"Basic block " + m_block.getId() + "\"];\n");
		} else {
			body.append(intToTab(indent) +getGraphvizName() + " [label = \"Block " + m_id + "\"];\n");
		}
		for (Node node : last) {
			connections.append( node.getGraphvizName() + " -> " + getGraphvizName() + ";\n");
		}
		
		next.add(this);
	}

	public String getGraphvizName() {
		return "Block" + m_id;
	}

	public void display(int indentLevel) {
		printIndent(indentLevel);
		System.out.printf("block(%d) ", m_id);
		
		for (XtaInstruction instruction : m_block.getInstructions()) {
			System.out.printf("[%s + %d: 0x%x]", 
					instruction.getFunctionName(),
					instruction.getFunctionOffset(),
					instruction.getPc());
		}
		System.out.printf("\n");
	}		
}
