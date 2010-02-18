package com.xmos.xta.core.engine.analysis.structural.nodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.Aborter;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.edges.Edges;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.core.engine.instructions.XtaInstruction;

public abstract class Node implements Comparable<Node> {
	public static int s_numNodes;
	
	private int m_id;
	protected Node m_parent;
	private boolean m_dominatorSetIsCompleteSet;
	private Edges m_edges;
	private boolean m_unresolved;
	private boolean m_infinite;
	private boolean m_isFolded;

	public enum Case {
		WORST,
		BEST,
	}

	public abstract String getTypeName();
	public abstract NodeAttributes getAttributes();	
	
	/**
	 * This copies all the structure of the node and everything within it but leaves all pointers to instruction
	 * pointing to the same instruction nodes. 
	 */
	public abstract Node copyStructure();
	
	/**
	 * This copies all the structure of the node and everything within it and also copies the instruction nodes
	 */
	public abstract Node deepCopy(Aborter abort) throws AbortedException;
	
	public abstract NodeTraverser.TraverseStatus traverse(Stack<Node> ancestors, NodeTraverser nodeTraverser);
	public abstract LinkedList<XtaInstruction> getInstructions();	
	public abstract TimeDistributionBuckets getTimeSpread();	
	
	//need to take a look at this one
	public abstract long getStartPc();
	
	//
	public abstract Set<InstructionNode> getInstructionNodes();
	public abstract Set<FunctionReferenceNode> getFunctionReferencesNodes();
	
	//these are for timing defines
	public abstract void replaceChild(Node oldNode, Node newNode);
	public abstract void replaceChildren(List<Node> newChildren);

	public Node() {
		m_id = s_numNodes++;
		m_edges = new Edges();
		m_isFolded = false;
	}
	
	public Node(Node node) {
		m_edges = new Edges();
		m_id = s_numNodes++;
		m_isFolded = false;
		m_unresolved = node.m_unresolved;
		m_infinite = node.m_infinite;
	}
	
	public boolean isCondNode()             { return false; }
	public boolean isInstructionNode()      { return false; }
	public boolean isFunctionNode()         { return false; }
	public boolean isFunctionReferenceNode(){ return false; }
	public boolean isLoopNode()             { return false; }
	public boolean isBlockNode()            { return false; }
	public boolean isSelfLoopNode()         { return false; }
	public boolean isNullNode()             { return false; }
	public boolean isInfiniteLoopNode()     { return false; }
	public boolean isInfiniteSelfLoopNode() { return false; }
	public boolean isEndNode()              { return false; }	
	public boolean isSequenceNode()         { return false; }

	public String toString() {
		return getTypeName() + m_id;
	}
		
	public void setUnresolved(boolean unresolved) {
		m_unresolved = unresolved;
	}
	
	public boolean isUnresolved() {
		return m_unresolved;
	}
	public void setInfinite(boolean infinite){
		m_infinite = infinite;
	}
	
	public boolean isInfinite(){
		return m_infinite;
	}
	
	public int getId() {
		return m_id;
	}

	public void setId(int id) {
		m_id = id;
	}
	
	public int compareTo(Node node) {
		return Integer.signum(m_id - node.m_id);
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
	
	public Node getParent(){
		return m_parent;
	}
	
	public void setParent(Node node) {
		m_parent = node;
	}
		
	public Node getEnclosingLoopNode() {		
		Node node = this;
		while ((node != null) && !node.isLoopNode() && !node.isSelfLoopNode()) {
			node = node.getParent();
		}
		return node;
	}

	public Node getEnclosingCondNode() {
		Node node = m_parent;
		while ((node != null) && !node.isCondNode()) {
			node = node.getParent();
		}
		return node;		
	}

	public Node getEnclosingOptionNode() {
		Node node = this;
		while ((node.getParent() != null) && !node.getParent().isCondNode()) {
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

	public Node getOption(Case c, Node ignoreIterationsAboveCond) {
		assert(false);
		return null;
	}
	
	public Node getUnsetOption(Case c, Node ignoreIterationsAboveCond) {
		assert(false);
		return null;
	}
	
	public List<Node> getAllUnsetOptions() {
		assert(false);
		return null;
	}
	
	public void setFolded(boolean folded, boolean applyToChildren) {
		m_isFolded = folded;
		if (applyToChildren) {
			for (Node child : iChildren()) {
				child.setFolded(folded, applyToChildren);
			}
		}
	}

	public boolean isFolded() {
		return m_isFolded;
	}
	
	public String getReferenceStr() {
		StringBuffer refStr = new StringBuffer();
		getBackTrail(refStr);
		if (isFunctionNode()) {
			refStr.append(((FunctionNode)this).getFunctionName());
		} else {
			refStr.append(String.format("0x%x", getStartPc()));
		}
		return refStr.toString();
	}
	
	public void getBackTrail(StringBuffer refStr) {
		if (m_parent != null) {
			m_parent.getBackTrail(refStr);
		}
	}
	
	protected void addFunctionRefToRefSet(Set<FunctionReferenceNode> fnRef,
			Node node) {
		if(node instanceof FunctionReferenceNode){
			fnRef.add((FunctionReferenceNode) node);
		}
		fnRef.addAll(node.getFunctionReferencesNodes());
	}
	
	public abstract List<Node> iChildren();
	public abstract Set<Node> structurGraphNext(Node childNode);
	public abstract void buildListUp(List<Node> nodeList, Stack<FunctionReferenceNode> stack, Node start);
	public abstract void buildListDown(List<Node> nodeList);
	
}

