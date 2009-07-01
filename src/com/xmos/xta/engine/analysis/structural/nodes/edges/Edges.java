package com.xmos.xta.engine.analysis.structural.nodes.edges;

import java.util.Set;
import java.util.TreeSet;

import com.xmos.xta.engine.analysis.structural.nodes.Node;

public class Edges {
	Set<Node> m_nextNodes;
	Set<Node> m_prevNodes;
	
	public Edges(){
	}
	
	public Edges(Edges edges){
		if(m_nextNodes != null){
			m_nextNodes = new TreeSet<Node>(edges.m_nextNodes);
		}
		if(m_prevNodes != null){
			m_prevNodes = new TreeSet<Node>(edges.m_prevNodes);
		}
	}
	
	public Set<Node> getNextNodes(){
		if(m_nextNodes == null){
			m_nextNodes = new TreeSet<Node>();
		}
		return m_nextNodes;
	}
	
	public Set<Node> getPrevNodes(){
		if(m_prevNodes == null){
			m_prevNodes = new TreeSet<Node>();
		}
		return m_prevNodes;
	}
	
	public int getNextNodesSize(){
		if(m_nextNodes == null) return 0;
		return m_nextNodes.size();
	}
	
	public int getPrevNodesSize(){
		if(m_prevNodes == null) return 0;
		return m_prevNodes.size();
	}
	
	public void clearNextNodes(){
		if(m_nextNodes == null) return;
		else m_nextNodes = null;
	}
	
	public void clearPrevNodes(){
		if(m_prevNodes == null) return;
		else m_prevNodes = null;
	}
	
	public void addNextNode(Node node){
		getNextNodes().add(node);
	}
	
	public void addPrevNode(Node node){
		getNextNodes().add(node);
	}
	
	
	
	
	public void removePrevNode(Node node){
		if(m_prevNodes == null) return;
		//assert(m_prevNodes.contains(node));
		if (m_prevNodes.contains(node)) {
			m_prevNodes.remove(node);
		}
	}
	
	public void removeNextNode(Node node){
		if(m_nextNodes == null) return;
		
		//assert(m_nextNodes.contains(node));
		if (m_nextNodes.contains(node)) {
			m_nextNodes.remove(node);
		}
	}
	
}
