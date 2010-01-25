package com.xmos.xta.core.engine.analysis.structural;

import java.util.Collection;
import java.util.LinkedList;
import com.xmos.xta.core.engine.analysis.structural.nodes.BlockNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionReferenceNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.NullNode;

public class BasicBlockMatcher {

	private BlockNode m_block;
	private Node m_treeStart;
	private Node m_lastInstruction;

	public boolean match(Node node, Node treeStart, Collection<Node> currentGraph) {
		m_treeStart = treeStart;
		LinkedList<Node> nodes = new LinkedList<Node>();
		boolean isStart = isStart(node);
		nodes.add(node);
		if (isStart(node)) {
			while (continueDown(node)) {
				node = node.getNextNode();
				nodes.add(node);
			}
		}
		m_lastInstruction = nodes.getLast();
		if (isStart) {
			LinkedList<InstructionNode> insts = new LinkedList<InstructionNode>();
			for(Node n : nodes){
				insts.add((InstructionNode)n);
			}
			
			m_block = new BlockNode(insts);
		}
		if (isStart && nodes.contains(treeStart)) {
			m_treeStart = m_block;
		} else {
			m_treeStart = treeStart;
		}
		return isStart;
	}

	public BlockNode getBlock() {
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
			if (prev instanceof FunctionReferenceNode) {
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
			
			if (next instanceof FunctionReferenceNode) {
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
