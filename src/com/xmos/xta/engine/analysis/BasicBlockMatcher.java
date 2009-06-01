package com.xmos.xta.engine.analysis;

import java.util.Collection;
import java.util.LinkedList;
import com.xmos.xta.engine.analysis.nodes.Block;
import com.xmos.xta.engine.analysis.nodes.Node;

public class BasicBlockMatcher  {

	private Block m_block;
	private Node m_treeStart;
	private Node m_lastBlock;
	
	public boolean match(Node node, Node treeStart, Collection<Node> currentGraph) {
		
		LinkedList<Node> insts = new LinkedList<Node>();
		boolean isStart = isStart(node);
		insts.add(node);
		if(isStart(node)){
			while(continueDown(node)){
				node = node.getNextNodes().iterator().next();
				insts.add(node);
			}
		}
		m_lastBlock = insts.getLast();
		if(isStart){
			m_block = new Block(insts);
		}
		if(isStart && insts.contains(treeStart)){
			m_treeStart = m_block;
		} else {
			m_treeStart = treeStart;
		}
		return isStart;
	}

	public Node getBlock() {
		return m_block;
	}
	
	public Node getTreeStart(){
		return m_treeStart;
	}
	
	private boolean isStart(Node node){
		if(node.getPrevNodes().size() ==  0){
			return true;
		} else if(node.getPrevNodes().size() ==  1){
			Node prev = node.getPrevNodes().iterator().next();
			if(prev.isFunction()){
				return true;
			}
			if(prev.getNextNodes().size() > 1){
				return true;
			} else {
				return false;
			}
		} else if(node.getPrevNodes().size() > 1){
			return true;
		} else {
			return false;
		}
	}
	
	private boolean isEnd(Node node){
		if(node.getNextNodes().size() ==  0){
			return true;
		} else if(node.getNextNodes().size() ==  1){
			Node next = node.getNextNodes().iterator().next();
			
			if(next.isFunction()){
				return true;
			}
			if(next.getPrevNodes().size() > 1){
				return true;
			} else {
				return false;
			}
		} else if(node.getNextNodes().size() > 1){
			return true;
		} else {
			return false;
		}
	}
	
	private boolean continueDown(Node node){
		if(node.getNextNodes().size() == 1 && !isEnd(node) 
				&& !node.getDominators().contains(node.getNextNodes().iterator().next())){
			return true;
		} else {
			return false;
		}
	}

	public Node getLastBlock() {
		return m_lastBlock;
	}
}
