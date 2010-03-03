package com.xmos.xta.core.engine.analysis.structural.nodes.edges;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class Edges {
	Set<Node> m_nextNodes;
	Set<Node> m_prevNodes;
	Set<Node> m_dominators;
	Set<Node> m_branchTargets;

	public Edges() {
		m_nextNodes = null;
		m_prevNodes = null;
		m_dominators = null;
		m_branchTargets = null;
	}

	public Node getNextNode() {
		assert(m_nextNodes != null);
		assert(m_nextNodes.size() == 1);
		return m_nextNodes.iterator().next();
	}

	public Node getPrevNode() {
		assert(m_prevNodes != null);
		assert(m_prevNodes.size() == 1);
		return m_prevNodes.iterator().next();
	}


	public Set<Node> getNextNodes(){
		if (m_nextNodes == null) {
			m_nextNodes = new TreeSet<Node>();
		}
		return m_nextNodes;
	}

	public Set<Node> getPrevNodes() {
		if(m_prevNodes == null){
			m_prevNodes = new TreeSet<Node>();
		}
		return m_prevNodes;
	}

	public int getNextNodesSize() {
		if(m_nextNodes == null) return 0;
		return m_nextNodes.size();
	}

	public int getPrevNodesSize() {
		if(m_prevNodes == null) return 0;
		return m_prevNodes.size();
	}

	public void clearNextNodes() {
		m_nextNodes = null;
	}

	public void addNextNode(Node node) {
		getNextNodes().add(node);
	}

	public void addPrevNode(Node node) {
		getPrevNodes().add(node);
	}

	public void removePrevNode(Node node) {
		if(m_prevNodes == null) return;
		if (m_prevNodes.contains(node)) {
			m_prevNodes.remove(node);
			if(m_prevNodes.isEmpty()){
				m_prevNodes = null;
			}
		}
	}

	public void removeNextNode(Node node) {
		if (m_nextNodes == null) {
			return;
		}

		if (m_nextNodes.contains(node)) {
			m_nextNodes.remove(node);
			if (m_nextNodes.isEmpty()) {
				m_nextNodes = null;
			}
		}
	}

	public Set<Node> getDominators() {
		if (m_dominators == null) {
			m_dominators = new TreeSet<Node>();
		}
		return m_dominators;
	}

	public boolean dominatorsContain(Node node) {
		if (m_dominators == null) {
			return false;
		} else {
			return m_dominators.contains(node);
		}
	}

	public void clearDominators() {
		m_dominators = null;
	}

	public void addDominator(Node node) {
		if (m_dominators == null) {
			m_dominators = new TreeSet<Node>();
		}
		m_dominators.add(node);
	}

	public void addAllDominators(Collection<Node> nodes) {
		if (m_dominators == null) {
			m_dominators = new TreeSet<Node>();
		}
		m_dominators.addAll(nodes);
	}

	public void addBranchTarget(Node newNode) {
		if (newNode == null) {
			return;
		}

		if (m_branchTargets == null) {
			m_branchTargets = new TreeSet<Node>();
		}
		m_branchTargets.add(newNode);
	}

	public Set<Node> getBranchTargets() {
		if (m_branchTargets == null) {
			m_branchTargets = new TreeSet<Node>();
		}
		return m_branchTargets;
	}

}
