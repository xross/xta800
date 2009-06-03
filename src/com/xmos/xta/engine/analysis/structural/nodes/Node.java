package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.xmos.xta.engine.instructions.XtaInstruction;

public abstract class Node implements Comparable<Node>{
	static int s_numNodes;
	
	int m_id;
	
	
	boolean m_dominatorSetIsCompleteSet;
	Set<Node> m_nextNodes;
	Set<Node> m_prevNodes;
	Node m_branchLinkReturn;
	Set<Node> m_dominators;

	public enum Case {
		WORST,
		BEST,
	}
	
	public abstract double getTimeTaken(Case c);
	public abstract long getNumCycles(Case c);
	public abstract long getNumInstructions(Case c);
	public abstract long getNumFNops(Case c);	
	
	/**
	 * this returns a set of all the PCs covered by the node regardless of the way its set up
	 */
	public abstract Set<Integer> getPCs();
	public abstract List<XtaInstruction> getInstructions();
	public abstract Collection<Node> getContaintedNodes();	
	public abstract String getFunctionName();
	public abstract boolean isFunction();
	public abstract int getStartPc();
	public abstract Node clone();
	public abstract Node deepClone(Map<Integer, FunctionNode> startPcs);
	public abstract void traverse(int depth, NodeTraverser nodeTraverser);

	public static int getNodeCount(){
		return s_numNodes;
	}
			
	public static void resetNodeCount() {
		s_numNodes = 0;
	}
	
	public Node() {
		m_nextNodes = new HashSet<Node>();
		m_prevNodes = new HashSet<Node>();
		m_dominators = new HashSet<Node>();
		m_id = s_numNodes++;
	}
	
	public Node(Node node) {
		m_nextNodes = new HashSet<Node>(node.m_nextNodes);
		m_prevNodes = new HashSet<Node>(node.m_prevNodes);
		m_dominators = new HashSet<Node>(node.m_prevNodes);
		m_id = s_numNodes++;
	}
	
	public int getId() {
		return m_id;
	}
	
	public int compareTo(Node n) {		
		if (getInstructions()!= null && 
			n.getInstructions() != null && 
			!getInstructions().isEmpty() && 
			!n.getInstructions().isEmpty()) {
			int myPc = getInstructions().get(0).getPc();
			int nodePc = n.getInstructions().get(0).getPc();
			return Integer.signum((nodePc - myPc));
		} 
		return 0;
	}
	
	public void setBranchLinkReturn(Node bl){
		m_branchLinkReturn = bl;
	}
	
	/**
	 * this returns the node that this block will return to after it has branch and linked.
	 * Will be null if this block doesn't branch and link.
	 */
	public Node getBranchLinkReturn() {
		return m_branchLinkReturn;
	}
	
	public Set<Node> getDominators() {
		return m_dominators;
	}
			
	public String toString(){
		return "Node" + m_id;  
	}
	
	public Set<Node> getNextNodes(){
		return m_nextNodes;
	}
	
	public Set<Node> getPrevNodes(){
		return m_prevNodes;
	}
	
	public void addNextNode(Node node){
		if (!m_nextNodes.contains(node)) {
			m_nextNodes.add(node);
		}
	}
	
	public void addPrevNode(Node node){
		if (!m_prevNodes.contains(node)) {
			m_prevNodes.add(node);
		}
	}
	
	public void removePrevNode(Node node){
		assert(m_prevNodes.contains(node));
		if (m_prevNodes.contains(node)) {
			m_prevNodes.remove(node);
		}
	}
	
	public void removeNextNode(Node node){
		assert(m_nextNodes.contains(node));
		if (m_nextNodes.contains(node)) {
			m_nextNodes.remove(node);
		}
	}
		
	public void setDominatorSetComplete(boolean b) {
		m_dominatorSetIsCompleteSet = b;
	}
	
	public boolean isDominatorSetComplete(){
		return m_dominatorSetIsCompleteSet;
	}
	
	public void freeMemory() {
		m_prevNodes = null;
		m_nextNodes = null;
		m_dominators = null;
		
	}
}
