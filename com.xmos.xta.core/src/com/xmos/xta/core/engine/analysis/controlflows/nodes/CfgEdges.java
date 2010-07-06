package com.xmos.xta.core.engine.analysis.controlflows.nodes;

import java.util.Collection;
import java.util.Set;

import com.xmos.libs.util.IteratableSet;

public class CfgEdges {
	Set<CfgNode> m_nextNodes;
	Set<CfgNode> m_prevNodes;
	Set<CfgNode> m_dominators;

	public CfgEdges() {
		m_nextNodes = null;
		m_prevNodes = null;
		m_dominators = null;
	}

	public CfgNode getNextNode() {
		assert(m_nextNodes != null);
		assert(m_nextNodes.size() == 1);
		return m_nextNodes.iterator().next();
	}

	public CfgNode getPrevNode() {
		assert(m_prevNodes != null);
		assert(m_prevNodes.size() == 1);
		return m_prevNodes.iterator().next();
	}

	public Set<CfgNode> getNextNodes(){
		if (m_nextNodes == null) {
			m_nextNodes = new IteratableSet<CfgNode>();
		}
		return m_nextNodes;
	}

	public Set<CfgNode> getPrevNodes() {
		if(m_prevNodes == null){
			m_prevNodes = new IteratableSet<CfgNode>();
		}
		return m_prevNodes;
	}


	public void addNextNode(CfgNode node) {
		getNextNodes().add(node);
	}

	public void addPrevNode(CfgNode node) {
		getPrevNodes().add(node);
	}

	public void removePrevNode(CfgNode node) {
		if(m_prevNodes == null) return;
		if (m_prevNodes.contains(node)) {
			m_prevNodes.remove(node);
			if(m_prevNodes.isEmpty()){
				m_prevNodes = null;
			}
		}
	}

	public void removeNextNode(CfgNode node) {
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

	public Set<CfgNode> getDominators() {
		if (m_dominators == null) {
			m_dominators = new IteratableSet<CfgNode>();
		}
		return m_dominators;
	}

	public boolean dominatorsContain(CfgNode node) {
		if (m_dominators == null) {
			return false;
		} else {
			return m_dominators.contains(node);
		}
	}

	public void clearDominators() {
		m_dominators = null;
	}

	public void addDominator(CfgNode node) {
		if (m_dominators == null) {
			m_dominators = new IteratableSet<CfgNode>();
		}
		m_dominators.add(node);
	}

	public void addAllDominators(Collection<CfgNode> nodes) {
		if (m_dominators == null) {
			m_dominators = new IteratableSet<CfgNode>();
		}
		m_dominators.addAll(nodes);
	}
}
