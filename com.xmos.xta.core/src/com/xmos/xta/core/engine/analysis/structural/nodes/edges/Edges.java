package com.xmos.xta.core.engine.analysis.structural.nodes.edges;

import java.util.Collection;
import java.util.Set;

import com.xmos.libs.util.IteratableSet;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class Edges {
	Set<Node> m_nextNodes;
	Set<Node> m_prevNodes;
	Set<Node> m_dominators;

	public Edges() {
		m_nextNodes = null;
		m_prevNodes = null;
		m_dominators = null;
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
			m_nextNodes = new IteratableSet<Node>();
		}
		return m_nextNodes;
	}

	public Set<Node> getPrevNodes() {
		if(m_prevNodes == null){
			m_prevNodes = new IteratableSet<Node>();
		}
		return m_prevNodes;
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
			m_dominators = new IteratableSet<Node>();
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
			m_dominators = new IteratableSet<Node>();
		}
		m_dominators.add(node);
	}

	public void addAllDominators(Collection<Node> nodes) {
		if (m_dominators == null) {
			m_dominators = new IteratableSet<Node>();
		}
		m_dominators.addAll(nodes);
	}

}
