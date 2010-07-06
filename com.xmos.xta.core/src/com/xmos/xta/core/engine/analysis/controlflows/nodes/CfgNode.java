package com.xmos.xta.core.engine.analysis.controlflows.nodes;

import java.util.Collection;
import java.util.Set;

import com.xmos.libs.util.IteratableSet;

public abstract class CfgNode implements Comparable<CfgNode> {
	public static int s_numNodes;

	private int m_id;
	private CfgEdges m_edges;
	private boolean m_unresolved;
	private boolean m_recursive;	
	private Set<CfgNode> m_branchReturnNodes;
	private boolean m_dominatorSetIsCompleteSet;

	public static void connect(CfgNode leaf, CfgNode root) {		
		root.addNextNode(leaf);
		leaf.addPrevNode(root);
	}
	
	public static void disconnect(CfgNode leaf, CfgNode root){
		root.removeNextNode(leaf);
		leaf.removePrevNode(root);
	}	
		
	public abstract CfgNode clone();
	public abstract String getTypeName();
	
	public CfgNode() {
		m_id = s_numNodes++;
		m_edges = new CfgEdges();
		m_unresolved = false;
		m_dominatorSetIsCompleteSet = false;
	}

	public CfgNode(CfgNode node) {
		m_id = s_numNodes++;
		m_edges = new CfgEdges();
		m_unresolved = node.m_unresolved;
		m_dominatorSetIsCompleteSet = node.m_dominatorSetIsCompleteSet;		
	}
	
	public int getId() {
		return m_id;
	}

	public int compareTo(CfgNode node) {
		return m_id - node.m_id;
	}
	
	public String toString() {
		return getTypeName() + m_id;
	}
	
	public Set<CfgNode> getDominators() {
		return m_edges.getDominators();
	}

	public boolean dominatorsContain(CfgNode node){
		return m_edges.dominatorsContain(node);
	}

	public void clearDominators(){
		m_edges.clearDominators();
	}

	public void addDominator(CfgNode node){
		m_edges.addDominator(node);
	}

	public void addAllDominators(Collection<CfgNode> nodes){
		m_edges.addAllDominators(nodes);
	}
	
	public void setDominatorSetComplete(boolean b) {
		m_dominatorSetIsCompleteSet = b;
	}

	public boolean isDominatorSetComplete(){
		return m_dominatorSetIsCompleteSet;
	}
	
	public Set<CfgNode> getNextNodes(){
		return m_edges.getNextNodes();
	}

	public Set<CfgNode> getPrevNodes(){
		return m_edges.getPrevNodes();
	}

	public CfgNode getNextNode(){
		return m_edges.getNextNode();
	}

	public CfgNode getPrevNode(){
		return m_edges.getPrevNode();
	}
	
	public int getNextNodesSize(){
		return m_edges.getNextNodes().size();
	}

	public void addNextNode(CfgNode node){
		m_edges.addNextNode(node);
	}

	public void addPrevNode(CfgNode node){
		m_edges.addPrevNode(node);
	}

	public void removePrevNode(CfgNode node){
		m_edges.removePrevNode(node);
	}

	public void removeNextNode(CfgNode node){
		m_edges.removeNextNode(node);
	}
	
	public void addBranchReturnNode(CfgInstructionNode node) {
		if(m_branchReturnNodes == null){
			m_branchReturnNodes = new IteratableSet<CfgNode>();
		}
		m_branchReturnNodes.add(node);		
	}	
	
	public Set<CfgNode> getBranchReturnNodes(){
		if(m_branchReturnNodes == null){
			m_branchReturnNodes = new IteratableSet<CfgNode>();
		}
		return m_branchReturnNodes;
	}
	
	public void setUnresolved(boolean unresolved) {
		m_unresolved = unresolved;
	}

	public boolean getUnresolved() {
		return m_unresolved;
	}
	
	public void setRecursive(boolean recursive) {
		m_recursive = recursive;
	}

	public boolean getRecursive() {
		return m_recursive;
	}	
}
