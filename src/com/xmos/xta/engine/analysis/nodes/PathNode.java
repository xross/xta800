package com.xmos.xta.engine.analysis.nodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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

	public double getTimeTaken() {
		double time = 0;
		for (Node node : m_nodes) {
			time += node.getTimeTaken();
		}
		return time;
	}
	
	public long getNumCycles() {
		long cycles = 0;
		for (Node node : m_nodes){
			cycles += node.getNumCycles();
		}
		return cycles;
	}

	public long getNumInstructions() {
		return getInstructions().size();
	}
	
	public long getNumFNops() {
		long fnops = 0;
		for(Node node : m_nodes){
			fnops += node.getNumFNops();
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

	public Set<Integer> getPCsCovered() {
		Set<Integer> pcs = new HashSet<Integer>();
		for(Node node : m_nodes){
			pcs.addAll(node.getPCsCovered());
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
	
	public String getGraphvizName() {
		return "cluster_Path" + m_id;
	}

	public boolean isFunction() {
		return false;
	}

	public int getStartPc() {
		return m_nodes.get(0).getStartPc();
	}

	public void traverse(int depth, NodeTraverser nodeTraverser) {
		nodeTraverser.foundPathNode(this, depth);
		for (Node node : m_nodes) {
			node.traverse(depth + 1, nodeTraverser);			
		}		
	}	
}
