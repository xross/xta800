package com.xmos.xta.core.engine.analysis.controlflows;

import java.util.Collection;
import java.util.LinkedList;

import com.xmos.xta.core.engine.analysis.controlflows.nodes.CfgBlockNode;
import com.xmos.xta.core.engine.analysis.controlflows.nodes.CfgFunctionReferenceNode;
import com.xmos.xta.core.engine.analysis.controlflows.nodes.CfgInstructionNode;
import com.xmos.xta.core.engine.analysis.controlflows.nodes.CfgNode;
import com.xmos.xta.core.engine.analysis.controlflows.nodes.CfgNullNode;

class BasicBlockMatcher {
	private CfgBlockNode m_block;
	private CfgNode m_treeStart;

	boolean match(CfgNode node, CfgNode treeStart, Collection<CfgNode> currentGraph) {
		m_treeStart = treeStart;
		LinkedList<CfgNode> nodes = new LinkedList<CfgNode>();
		boolean isStart = isStart(node);
		nodes.add(node);
		if (isStart(node)) {
			while (continueDown(node)) {
				node = node.getNextNode();
				nodes.add(node);
			}
		}
		if (isStart) {
			LinkedList<CfgInstructionNode> insts = new LinkedList<CfgInstructionNode>();
			for (CfgNode n : nodes) {
				insts.add((CfgInstructionNode)n);
			}

			m_block = new CfgBlockNode(insts);
		}
		if (isStart && nodes.contains(treeStart)) {
			m_treeStart = m_block;
		} else {
			m_treeStart = treeStart;
		}
		return isStart;
	}

	CfgBlockNode getBlock() {
		return m_block;
	}

	CfgNode getTreeStart() {
		return m_treeStart;
	}

	private boolean isStart(CfgNode node) {
		if (node == m_treeStart) {
			return true;
			
		} else if (node.getPrevNodes().size() == 0) {
			return true;
			
		} else if (node.getPrevNodes().size() == 1) {
			CfgNode prev = node.getPrevNode();
			if (prev instanceof CfgFunctionReferenceNode) {
				return true;
			}
			if (prev.getNextNodesSize() > 1) {
				return true;
				
			} else {
				return false;
			}
		} else if (node.getPrevNodes().size() > 1) {
			return true;
			
		} else {
			return false;
		}
	}

	private boolean isEnd(CfgNode node) {

		if (node.getNextNodes().size() == 0) {
			return true;
		} else if (node.getNextNodes().size() == 1) {
			CfgNode next = node.getNextNode();
			if(next == m_treeStart){
				return true;
			}
			if(next instanceof CfgNullNode ){
				return true;
			}

			if (next instanceof CfgFunctionReferenceNode) {
				return true;
			}
			if (next.getPrevNodes().size() > 1) {
				return true;
			} else {
				return false;
			}
		} else if (node.getNextNodes().size() > 1) {
			return true;
		} else {
			return false;
		}
	}

	private boolean continueDown(CfgNode node) {
		if (node.getNextNodesSize() == 1 && 
			!isEnd(node) && 
			!node.dominatorsContain(node.getNextNode())) {
			return true;
			
		} else {
			return false;
		}
	}
}
