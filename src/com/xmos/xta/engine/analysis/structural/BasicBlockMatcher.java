package com.xmos.xta.engine.analysis.structural;

import java.util.Collection;
import java.util.LinkedList;
import com.xmos.xta.engine.analysis.structural.nodes.BlockNode;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.NullNode;

public class BasicBlockMatcher {

	private BlockNode m_block;
	private Node m_treeStart;
	private Node m_lastInstruction;

	public boolean match(Node node, Node treeStart, Collection<Node> currentGraph) {
		m_treeStart = treeStart;
		LinkedList<Node> insts = new LinkedList<Node>();
		boolean isStart = isStart(node);
		insts.add(node);
		if (isStart(node)) {
			while (continueDown(node)) {
				node = node.getNextNode();
				insts.add(node);
			}
		}
		m_lastInstruction = insts.getLast();
		if (isStart) {
			m_block = new BlockNode(insts);
		}
		if (isStart && insts.contains(treeStart)) {
			m_treeStart = m_block;
		} else {
			m_treeStart = treeStart;
		}
		return isStart;
	}

	public Node getBlock() {
		return m_block;
	}

	public Node getTreeStart() {
		return m_treeStart;
	}

	private boolean isStart(Node node) {
		if(node == m_treeStart){
			return true;
		} else 
			if (node.getPrevNodesSize() == 0) {
			return true;
		} else if (node.getPrevNodesSize() == 1) {
			Node prev = node.getPrevNode();
			if (prev.isFunctionNode()) {
				return true;
			}
			if (prev.getNextNodesSize() > 1) {
				return true;
			} else {
				return false;
			}
		} else if (node.getPrevNodesSize() > 1) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isEnd(Node node) {
		
		
		if (node.getNextNodesSize() == 0) {
			return true;
		} else if (node.getNextNodesSize() == 1) {
			Node next = node.getNextNode();
			if(next == m_treeStart){
				return true;
			}
			if(next instanceof NullNode ){
				return true;
			}
			
			if (next.isFunctionNode()) {
				return true;
			}
			if (next.getPrevNodesSize() > 1) {
				return true;
			} else {
				return false;
			}
		} else if (node.getNextNodesSize() > 1) {
			return true;
		} else {
			return false;
		}
	}

	private boolean continueDown(Node node) {
		if (node.getNextNodesSize() == 1 && !isEnd(node)
				&& !node.dominatorsContain(node.getNextNode())) {
			return true;
		} else {
			return false;
		}
	}

	public Node getLastBlock() {
		return m_lastInstruction;
	}
}
