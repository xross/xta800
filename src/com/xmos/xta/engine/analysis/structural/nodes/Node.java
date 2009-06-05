package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.xmos.xta.engine.instructions.XtaInstruction;
import com.xmos.xta.engine.references.types.ReferenceType;

public abstract class Node implements Comparable<Node>{
	static int s_numNodes;
	
	int m_id;
	
	Node m_parent;
	Collection<Node> m_next;
	
	boolean m_dominatorSetIsCompleteSet;
	Set<Node> m_nextNodes;
	Set<Node> m_prevNodes;
	Node m_branchLinkReturn;
	Set<Node> m_dominators;
	
	long m_numIterations;
	boolean m_numIterationsDone;
	boolean m_isAbsolute;
	
	boolean isExcluded;

	public enum Case {
		WORST,
		BEST,
	}

	public abstract Node clone();
	public abstract Node deepClone(Map<Integer, FunctionNode> startPcs);
	public abstract Collection<Node> getChildern();
	public abstract Collection<Node> whatsNext(Node node);
	public abstract void traverse(int depth, NodeTraverser nodeTraverser);
	
	public abstract double getTimeTaken(Case c);
	public abstract long getNumCycles(Case c);
	public abstract long getNumInstructions(Case c);
	public abstract long getNumFNops(Case c);	
	
	public abstract Set<Integer> getPCs();
	public abstract List<XtaInstruction> getInstructions();
	public abstract Collection<Node> getContaintedNodes();	
	public abstract String getFunctionName();
	public abstract int getStartPc();
	
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
		m_prevNodes.clear();
		m_nextNodes.clear();
		m_dominators.clear();
	}
	
	public Node getParent(){
		return m_parent;
	}
	
	public void setParent(Node node) {
		m_parent = node;
	}
	
	public boolean isInstructionNode() {
		return false;
	}
	
	public boolean isFunctionNode() {
		return false;
	}

	public boolean isLoopNode() {
		return false;
	}
	
	public void setNumIterations(long numIterations) {
	    if (!m_numIterationsDone) {
	    	m_numIterations = numIterations;
	    	m_numIterationsDone = true;
	    	
			for (Node child : getChildern()) {
				if (child.isLoopNode()) {
					// Just set the number of iterations on the loop, 
					// but don't propagate it down to its children
					child.m_numIterations = numIterations;
				} else {
				    child.setNumIterations(numIterations);
				}
			}
			m_parent.setNumIterations(numIterations);
	     }
	}
	
	public long getNumIterations() {
		return m_numIterations;
	}
	
	public void setNumIterationsDone(boolean done) {
		m_numIterationsDone = done;
		for (Node child : getChildern()) {
			if (!child.isLoopNode()) {
			    child.setNumIterationsDone(done);
			}
		}
	}
	
	public boolean getNumIterationsDone() {
		return m_numIterationsDone;
	}
	
	public void setScope(boolean isAbsolute) {
		m_isAbsolute = isAbsolute;
	}
	
	public boolean getScope() {
		return m_isAbsolute;
	}
	
	public Node getEnclosingLoopNode() {		
		Node node = m_parent;
		while ((node != null) && !node.isLoopNode()) {
			node = node.getParent();
		}
		return node;
	}

	public boolean checkBackTrail(Stack<ReferenceType> referenceTypes) {
		return checkBackTrail(referenceTypes, null, false, new FunctionCallStatus());
	}
	
	boolean checkBackTrail(Stack<ReferenceType> referenceTypes, Node node, 
			boolean calledFromAbove, FunctionCallStatus functionCallStatus) {
		
		// Builds up a stack of all the children up to the calling node...
		Stack<Node> children = new Stack<Node>();
		for (Node child : getChildern()) {
			if (child == node)
				break;
			
			children.push(child);
		}
		
		// Now call each of the children.
		// Note: the use of a stack causes 
		// them to be called in reverse order.
		while (!children.empty()) {
			Node child = children.pop();			
			if (child.checkBackTrail(referenceTypes, this, true, functionCallStatus))
				return true;
		}
		
		// Now if we are on are way up, then call the parent...
		if (!calledFromAbove) {
			if (m_parent == null) {
				// We have reached the top of the tree...
				return false;
				
			} else {		
		        if (m_parent.checkBackTrail(referenceTypes, this, false, functionCallStatus))
		    	    return true;
			}
		}
		return false;
	}			
}

class FunctionCallStatus {
	private boolean m_status;
	
	FunctionCallStatus() {
		m_status = false;
	}
	
	void setStatus(boolean status) { m_status = status; }
	boolean getStatus()            { return m_status; }
}
