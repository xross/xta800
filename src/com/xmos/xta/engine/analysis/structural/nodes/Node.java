package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.xmos.xta.engine.analysis.controlflow.InstructionBuffer;
import com.xmos.xta.engine.instructions.XtaInstruction;
import com.xmos.xta.engine.references.types.ReferenceType;

public abstract class Node implements Comparable<Node>{
	static int s_numNodes;
	
	int m_id;
	
	Node m_parent;
	
	boolean m_dominatorSetIsCompleteSet;
	Set<Node> m_nextNodes;
	Set<Node> m_prevNodes;
	Node m_branchLinkReturn;
	Set<Node> m_dominators;
	
	long m_numIterations;
	boolean m_numIterationsDone;
	boolean m_isAbsolute;	
	boolean m_isExcluded;
	boolean m_isLoopHeader;

	Set<Node> m_branchTargets;
	Map<Integer, IBufferSequence> m_ibufferStates;
	public enum Case {
		WORST,
		BEST,
	}

	public abstract Node shallowCopy();
	public abstract Node deepCopy(Map<Integer, FunctionNode> startPcs);
	
	public abstract double getTimeTakenRelative(Case c);
	public abstract double getTimeTakenAbsolute(Case c);
	public abstract long getNumCyclesRelative(Case c);
	public abstract long getNumCyclesAbsolute(Case c);
	public abstract long getNumInstructionsRelative(Case c);
	public abstract long getNumInstructionsAbsolute(Case c);
	public abstract long getNumFNopsRelative(Case c);	
	public abstract long getNumFNopsAbsolute(Case c);	
	
	public abstract List<Node> getChildren();
	public abstract Collection<Node> whatsNext(Node node);
	public abstract void traverse(int depth, NodeTraverser nodeTraverser);
	public abstract List<XtaInstruction> getInstructionStream(Case c);
	

	public abstract Set<Integer> getPCs();
	
	public abstract LinkedList<XtaInstruction> getInstructions();
	public abstract Collection<Node> getContainedNodes();	
	public abstract String getFunctionName();
	public abstract int getStartPc();
	
	public static int getNodeCount(){
		return s_numNodes;
	}

	public static void resetNodeCount() {
		s_numNodes = 0;
	}
	
	public Node() {
		m_isExcluded = false;
		m_isLoopHeader = false;
		m_ibufferStates = new HashMap<Integer, IBufferSequence>();
		m_branchTargets = new HashSet<Node>();
		m_nextNodes = new HashSet<Node>();
		m_prevNodes = new HashSet<Node>();
		m_dominators = new HashSet<Node>();
		m_id = s_numNodes++;
	}
	
	public Node(Node node) {
		m_isExcluded = node.m_isExcluded;
		m_isLoopHeader = node.m_isLoopHeader;
		m_ibufferStates = new HashMap<Integer, IBufferSequence>(node.m_ibufferStates);
		m_nextNodes = new HashSet<Node>(node.m_nextNodes);
		m_prevNodes = new HashSet<Node>(node.m_prevNodes);
		m_dominators = new HashSet<Node>(node.m_prevNodes);
		m_id = s_numNodes++;
	}
	
	public Collection<InstructionBuffer> getEndInstructionBuffers(){
		HashSet<InstructionBuffer> ibuffers = new HashSet<InstructionBuffer>();
		
		for(IBufferSequence ibs : m_ibufferStates.values()){
			ibuffers.add(ibs.m_endInstBuffer);
		}
		
		return ibuffers;
	}
	
	public int getId() {
		return m_id;
	}

	public double getTimeTaken(Case c) {
		return getTimeTakenRelative(c) + getTimeTakenAbsolute(c);
	}
	
	public long getNumCycles(Case c) {
		return getNumCyclesRelative(c) + getNumCyclesAbsolute(c);		
	}
	
	public long getNumInstructions(Case c) {
		return getNumInstructionsRelative(c) + getNumInstructionsAbsolute(c);				
	}
	
	public long getNumFNops(Case c) {
		return getNumFNopsRelative(c) + getNumFNopsAbsolute(c);				
	}
	
	public int compareTo(Node node) {	
		double thisTimeTaken = getTimeTaken(Case.WORST);
		double nodeTimeTaken = node.getTimeTaken(Case.WORST);
		
		// First compare based on time taken...
		if (thisTimeTaken > nodeTimeTaken) {
			return 1;
			
		} else if (thisTimeTaken < nodeTimeTaken) {
		    return -1;
		    
		} else {
			// If the time taken is the same, then 
			// use the pcs to define the ordering..
			if (getInstructions()!= null && 
				node.getInstructions() != null && 
				!getInstructions().isEmpty() && 
				!node.getInstructions().isEmpty()) {
				int myPc = getInstructions().get(0).getPc();
				int nodePc = node.getInstructions().get(0).getPc();
				return Integer.signum((nodePc - myPc));
			} 
			return 0;			
		}			
    }
	
	public void setBranchLinkReturn(Node bl){
		m_branchLinkReturn = bl;
	}
	
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
	
	public boolean isSectionNode() {
		return false;
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
	    	
			for (Node child : getChildren()) {
				if (child.isLoopNode()) {
					// Just set the number of iterations on the loop, 
					// but don't propagate it down to its children
					child.m_numIterations = numIterations;
				} else {
				    child.setNumIterations(numIterations);
				}
			}
			
			if (m_parent != null)
			    m_parent.setNumIterations(numIterations);
	     }
	}
	
	public long getNumIterations() {
		return m_numIterations;
	}
	
	public void setNumIterationsDone(boolean done) {
		m_numIterationsDone = done;
		for (Node child : getChildren()) {
			if (!child.isLoopNode()) {
			    child.setNumIterationsDone(done);
			}
		}
	}
	
	public boolean getNumIterationsDone() {
		return m_numIterationsDone;
	}
	
	public void setIsAbsolute(boolean isAbsolute) {
		m_isAbsolute = isAbsolute;
		for (Node child : getChildren()) {
			if (!child.isLoopNode()) {
			    child.setIsAbsolute(isAbsolute);
			}
		}
	}
	
	public boolean getIsAbsolute() {
		return m_isAbsolute;
	}
	
	public void setIsExcluded(boolean isExcluded) {
		m_isExcluded = isExcluded;
	}
	
	public boolean isExcluded() {
		return m_isExcluded;
	}	
	
	public void setIsLoopHeader(boolean isLoopHeader) {
		m_isLoopHeader = isLoopHeader;
	}

	public boolean isLoopHeader() {
		return m_isLoopHeader;
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
		for (Node child : getChildren()) {
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
	
	public Set<Node> getBranchTargets(){
		return m_branchTargets;
	}
	
	public boolean branchesTo(Node block) {
		return m_branchTargets.contains(block);
	}

	class IBufferSequence {
		InstructionBuffer m_endInstBuffer;
		List<Integer> m_fnopPcs;
		
		public IBufferSequence(InstructionBuffer endInstBuffer, List<Integer> fnopPcs) {
			super();
			m_endInstBuffer = endInstBuffer;
			m_fnopPcs = fnopPcs;
		}
	}
	
	
	public Map<Integer, IBufferSequence> getIbufferStates(){
		return m_ibufferStates;
	}
	
	public boolean addEndInstructionBuffer(int numStartBytes, InstructionBuffer endInstBuffer, List<Integer> fnopPcs) {
		if(!m_ibufferStates.containsKey(numStartBytes)){
			m_ibufferStates.put(numStartBytes, new IBufferSequence(endInstBuffer, fnopPcs));
			return true;
		} else {
			return false;
		}
	}	
	
	public int getFnopCountDiff(){
		Set<Integer> set = new HashSet<Integer>();
		for(IBufferSequence i : m_ibufferStates.values()){
			set.add(i.m_fnopPcs.size());
		}
		return set.size();
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
