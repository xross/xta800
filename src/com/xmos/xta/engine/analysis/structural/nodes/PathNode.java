package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.xmos.xta.engine.instructions.XtaInstruction;

public class PathNode extends Node {
	List<Node> m_nodes;
	
	public PathNode(List<Node> nodes) {
		super();
		assert(nodes!= null);
		assert(nodes.size()>1);
		m_nodes = nodes;
	}

	public PathNode(PathNode path) {
		super();
		m_nodes = path.m_nodes;
	}

	public PathNode() {
		super();
		m_nodes = new LinkedList<Node>();
	}
	
	public Node deepClone(Map<Integer, FunctionNode> startPcs) {
		PathNode pn = new PathNode();
		for (Node path : m_nodes) {
			Node clone = path.deepClone(startPcs);
			pn.m_nodes.add(clone);
		}
		return pn;
	}

	public double getTimeTaken(Case c) {
		double time = 0;
	    for (Node node : m_nodes) {
		    time += node.getTimeTaken(c);
		}
		return time;
	}
	
	public long getNumCycles(Case c) {
   		long cycles = 0;
    	for (Node node : m_nodes){
	    	cycles += node.getNumCycles(c);
		}
		return cycles;
	}

	public long getNumInstructions(Case c) {
   		return getInstructions().size();
	}
	
	public long getNumFNops(Case c) {
		long fnops = 0;
   		for(Node node : m_nodes){
    		fnops += node.getNumFNops(c);
		}
		return fnops;
	}
	
	public List<XtaInstruction> getInstructions() {
		List<XtaInstruction> list = new LinkedList<XtaInstruction>();
		for(Node node : m_nodes){
			list.addAll(node.getInstructions());
		}
		return list;
	}

	public Set<Integer> getPCs() {
		Set<Integer> pcs = new HashSet<Integer>();
		for(Node node : m_nodes){
			pcs.addAll(node.getPCs());
		}
		return pcs;
	}

	public Node clone() {
		return new PathNode(this);
	}

	public Collection<Node> getContaintedNodes() {
		Collection<Node> containedNodes = new HashSet<Node> ();
		for(Node loop: m_nodes){
			containedNodes.addAll(loop.getContaintedNodes());
		}
		return containedNodes;
	}

	public String getFunctionName() {
		return "";
	}
	

	public boolean isFunction() {
		return false;
	}

	public int getStartPc() {
		return m_nodes.get(0).getStartPc();
	}

	public void traverse(int depth, NodeTraverser nodeTraverser) {
		if (!nodeTraverser.foundPathNode(this, depth))
			return;
		
		for (Node node : m_nodes) {
			node.traverse(depth + 1, nodeTraverser);			
		}		
	}
	
	public String toString(){
		return "Path" + m_id;
	}

	public Collection<Node> getChildern() {
		return m_nodes;
	}
	
	public Collection<Node> whatsNext(Node node) {
		assert(m_nodes.contains(node));
		int index = m_nodes.indexOf(node);
		if(index < m_nodes.size()-1){
			Collection<Node> next = new HashSet<Node>();
			next.add(m_nodes.get(index+1));
			return next;
		} else {
			return getParent().whatsNext(this);
		}
	}
}
