package com.xmos.xta.engine.xtagraph.nodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.xmos.xta.engine.XtaInstruction;

public abstract class Node implements Comparable<Node>{
	
	protected static int id;
	protected int m_id;
	
	public static int getNodeCount(){
		return id;
	}
	
	protected Set<Node> m_nextNodes;
	protected Set<Node> m_prevNodes;
	protected Node m_branchLinkReturn;
	protected Set<Node> m_dominators;
	
	public Node(){
		m_nextNodes = new HashSet<Node>();
		m_prevNodes = new HashSet<Node>();
		m_dominators = new HashSet<Node>();
		m_id = id++;
	}
	
	public static void resetId(){
		id = 0;
	}
	public int compareTo(Node n) {
		
		if(getInstructions()!= null && n.getInstructions() != null 
				&& !getInstructions().isEmpty() && !n.getInstructions().isEmpty()){
			int myPc = getInstructions().get(0).getPc();
			int nodePc = n.getInstructions().get(0).getPc();
			return Integer.signum((nodePc - myPc));
		} else {
			return 0;
		}
		
	}
	
	public void setBranchLinkReturn(Node bl){
		m_branchLinkReturn = bl;
	}
	
	public Node getBranchLinkReturn(){
		return m_branchLinkReturn;
	}
	
	public Set<Node> getDominators(){
		return m_dominators;
	}
	
	public Node getIdom(){
		Set<Node> remainingDominators = new HashSet<Node>(m_dominators);
		remainingDominators.remove(this);
		for(Node dom : m_dominators){
			if(dom == this) continue;
			Set<Node> set = new HashSet<Node>(dom.getDominators());
			set.remove(dom);
			remainingDominators.removeAll(set);
		}
		if(remainingDominators.size() == 1){
			return remainingDominators.iterator().next();
		} else {
			return this;
		}
	}
	public static String intToTab(int indent){
		String s = "";
		for(int i=0;i<indent;i++){
			s +="\t";
		}
		return s;
	}
	
	public String toString(){
		return "Node " + m_id;  
	}
	
	public String graphVizLabel(){
		return "Node" + m_id;  
	}
	
	public Set<Node> getNextNodes(){
		return m_nextNodes;
	}
	
	public Set<Node> getPrevNodes(){
		return m_prevNodes;
	}
	
	public void addNextNode(Node node){
		//assert(!m_nextNodes.contains(node));
		if(!m_nextNodes.contains(node)){
			m_nextNodes.add(node);
		}
	}
	
	public void addPrevNode(Node node){
		//assert(!m_prevNodes.contains(node));
		if(!m_prevNodes.contains(node)){
			m_prevNodes.add(node);
		}
	}
	
	public void removePrevNode(Node node){
		assert(m_prevNodes.contains(node));
		if(m_prevNodes.contains(node)){
			m_prevNodes.remove(node);
		}
	}
	
	public void removeNextNode(Node node){
		assert(m_nextNodes.contains(node));
		if(m_nextNodes.contains(node)){
			m_nextNodes.remove(node);
		}
	}
	
	public long getNumberInstructions(){
		return getInstructions().size();
	}
	
	public abstract Node clone();
	
	public abstract Set<Integer> getPCsCovered();
	public abstract long getNumberCycles();
	public abstract long getNumberFNOPS();
	
	public abstract void display(int indentLevel);
	
	/**
	 * return a list of instruction in order without the fnops inserted.
	 */
	public abstract List<XtaInstruction> getInstructions();
	
	/**
	 * returns list of instructions in order with the fnops inserted.
	 */
	public abstract List<XtaInstruction> getInstructionStream();
	
	public abstract Collection<Node> getContaintedNodes();
	
	public abstract void appendToGraph(StringBuffer body, StringBuffer connections,
			Collection<Node> last, Collection<Node> next, int indent, Node parent);
	
	public abstract String getGraphvizName();
	
	public abstract boolean isFunction();
	
	public abstract int getStartPc();
	
	protected void printIndent(int indentLevel) {
		for (int i = 0; i < indentLevel; ++i) {
		    System.out.printf(".    ");
		}
	}

	

}
