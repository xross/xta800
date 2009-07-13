package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.Status;
import com.xmos.xta.engine.analysis.structural.nodes.edges.Edges;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.engine.instructions.XtaInstruction;
import com.xmos.xta.engine.references.types.ReferenceType;

public abstract class Node implements Comparable<Node>{
	public static int s_numNodes;
	
	int m_id;
	Node m_parent;
	boolean m_dominatorSetIsCompleteSet;
	Edges m_edges;
	Node m_branchLinkReturn;
	
	boolean m_unresolved;
	
	private boolean m_isFolded;

	public enum Case {
		WORST,
		BEST,
	}

	public abstract String getTypeName();
	public abstract NodeAttributes getAttributes();
	
	public abstract Node shallowCopy();
	public abstract Node deepCopy();
			
	public abstract List<Node> getChildren();
	public abstract List<Node> getImmediateChildren();
	public abstract Collection<Node> whatsNext(Node node);
	public abstract Collection<Node> whatsBefore(Node node);
	public abstract void traverse(int depth, NodeTraverser nodeTraverser);
	public abstract Set<Integer> getPCs();	
	public abstract LinkedList<XtaInstruction> getInstructions();
	
	public abstract TimeDistributionBuckets getTimeSpread();
	
	/**
	 * This returns all the instruction nodes that could possible be contained within this node
	 */
	public abstract Collection<Node> getContainedNodes();	
	public abstract int getStartPc();
	
	
	public Node() {
		m_id = s_numNodes++;
		m_edges = new Edges();
		m_isFolded = false;
	}
	
	public Node(Node node) {
		//FIXME i dont think any of this copying comes to any use!
		m_edges = new Edges(node.m_edges);
		m_id = s_numNodes++;
		m_isFolded = false;
		m_unresolved = node.m_unresolved;
	}
	
	public void setUnresolved(boolean unresolved){
		m_unresolved = unresolved;
	}
	
	public boolean isUnresolved(){
		return m_unresolved;
	}
	
	public int getId() {
		return m_id;
	}
	
	private static final boolean s = false;
	public int compareTo(Node node) {
		if (s) {
			Status status = new Status();
			double thisTimeTaken = getAttributes().getTimeTaken(Case.WORST, status, null);
			double nodeTimeTaken = node.getAttributes().getTimeTaken(Case.WORST, status, null);

			if (thisTimeTaken - nodeTimeTaken != 0) {
				return Integer.signum((int) (thisTimeTaken - nodeTimeTaken));
			} else {

				return Integer.signum(m_id - node.m_id);
			}
		} else {
			return Integer.signum(m_id - node.m_id);
		}
	}
	
	public void setBranchLinkReturn(Node bl){
		m_branchLinkReturn = bl;
	}
	
	public Node getBranchLinkReturn() {
		return m_branchLinkReturn;
	}
	
	public Set<Node> getDominators() {
		return m_edges.getDominators();
	}
			
	public boolean dominatorsContain(Node node){
		return m_edges.dominatorsContain(node);
	}
	
	public void clearDominators(){
		m_edges.clearDominators();
	}
	
	public void addDominator(Node node){
		m_edges.addDominator(node);
	}
	
	public void addAllDominators(Collection<Node> nodes){
		m_edges.addAllDominators(nodes);
	}

	public String toString(){
		return "Node" + m_id;  
	}
	
	public Set<Node> getNextNodes(){
		return m_edges.getNextNodes();
	}
	
	public Set<Node> getPrevNodes(){
		return m_edges.getPrevNodes();
	}
	
	public Node getNextNode(){
		return m_edges.getNextNode();
	}
	
	public Node getPrevNode(){
		return m_edges.getPrevNode();
	}
	
	public void clearNextNodes(){
		m_edges.clearNextNodes();
	}
	
	public void clearPrevNodes(){
		m_edges.clearPrevNodes();
	}
	
	public int getNextNodesSize(){
		return m_edges.getNextNodesSize();
	}
	
	public int getPrevNodesSize(){
		return m_edges.getPrevNodesSize();
	}
	
	public void addNextNode(Node node){
		m_edges.addNextNode(node);
	}
	
	public void addPrevNode(Node node){
		m_edges.addPrevNode(node);
	}
	
	public void removePrevNode(Node node){
		m_edges.removePrevNode(node);
	}
	
	public void removeNextNode(Node node){
		m_edges.removeNextNode(node);
	}

	public void setDominatorSetComplete(boolean b) {
		m_dominatorSetIsCompleteSet = b;
	}
	
	public boolean isDominatorSetComplete(){
		return m_dominatorSetIsCompleteSet;
	}
	
	public void freeMemory() {
		m_edges.freeMemory();
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

	public boolean isBlockNode() {
		return false;
	}
	
	public boolean isSelfLoopNode() {
		return false;
	}

	public boolean isTerminalNode() {
		return false;
	}	

	public boolean isNullNode() {
		return false;
	}

	public boolean isInfiniteLoopNode(){
		return false;
	}
	
	public boolean isInfiniteSelfLoopNode() {
		return false;
	}
	
	public Node getEnclosingLoopNode() {		
		Node node = this;
		while ((node != null) && !node.isLoopNode() && !node.isSelfLoopNode()) {
			node = node.getParent();
		}
		return node;
	}

	public Node getEnclosingSectionNode() {
		Node node = m_parent;
		while ((node != null) && !node.isSectionNode()) {
			node = node.getParent();
		}
		return node;		
	}

	public Node getEnclosingOptionNode() {
		Node node = this;
		while ((node.getParent() != null) && !node.getParent().isSectionNode()) {
			node = node.getParent();
		}
		return node;		
	}
	
	public Node getEnclosingInfiniteLoopNode() {		
		Node node = this;
		while ((node != null) && !node.isInfiniteLoopNode() && !node.isInfiniteSelfLoopNode()) {
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
	
	public String getBackTrace(String backTrace) {
		if (m_parent != null) {
			return m_parent.getBackTrace(backTrace);
		}
		return backTrace;
	}
	
	public void addBranchTarget(Node newNode){
		m_edges.addBranchTarget(newNode);
	}
	
	public Set<Node> getBranchTargets(){
		return m_edges.getBranchTargets();
	}
	
	public boolean branchesTo(Node block) {
		return m_edges.branchesTo(block);
	}
		
	public String getDescription() {
		return "";
	}
	
	public String getFunctionName() {
		return "";
	}

	public Node getOption(Case c, Node ignoreIterationsAboveSection) {
		assert(false);
		return null;
	}
	
	public Node getUnsetOption(Case c, Node ignoreIterationsAboveSection) {
		assert(false);
		return null;
	}
	
	public List<Node> getAllUnsetOptions() {
		assert(false);
		return null;
	}

	public boolean isSectionOptional() {
		return false;
	}
	
	public void setFolded(boolean folded, boolean applyToChildren) {
		m_isFolded = folded;
		if (applyToChildren) {
			for (Node child : getChildren()) {
				child.setFolded(folded, applyToChildren);
			}
		}
	}

	public boolean isFolded() {
		return m_isFolded;
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
