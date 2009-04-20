package com.xmos.xta.engine.xtagraph.nodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.xmos.xta.engine.XtaInstruction;

public abstract class Node {
	
	protected static int id;
	protected int m_id;
	
	
	//TODO - next nodes should be a divided into branching and non branching nextNodes
	protected Set<Node> m_nextNodes;
	protected Set<Node> m_prevNodes;
	
	
	public Node(){
		m_nextNodes = new HashSet<Node>();
		m_prevNodes = new HashSet<Node>();
		m_id = id++;
	}
	
	public String toString(){
		return "Node " + m_id;  
	}
	
	public String graphVizLabel(){
		return "Node" + m_id;  
	}
	/*
	public String graphVizLinks(){
		StringBuffer sb = new StringBuffer();
		for(Node next : m_nextNodes){
			sb.append("Node" + m_id + " -> Node" + next.m_id + ";\n");
		}
		return sb.toString();
	}
	*/
	
	public Set<Node> getNextNodes(){
		return m_nextNodes;
	}
	
	public Set<Node> getPrevNodes(){
		return m_prevNodes;
	}
	
	public void addNextNode(Node node){
		m_nextNodes.add(node);
	}
	
	public void addPrevNode(Node node){
		m_prevNodes.add(node);
	}
	
	public long getNumberInstructions(){
		return getInstructions().size();
	}
	
	public abstract Node clone();
	
	public abstract Set<Integer> getPCsCovered();
	public abstract long getNumberCycles();
	public abstract long getNumberFNOPS();
	
	/**
	 * return a list of instruction in order without the fnops inserted.
	 */
	public abstract List<XtaInstruction> getInstructions();
	
	/**
	 * returns list of instructions in order with the fnops inserted.
	 */
	public abstract List<XtaInstruction> getInstructionStream();
	
	public abstract Collection<Node> getContaintedNodes();
	
}
