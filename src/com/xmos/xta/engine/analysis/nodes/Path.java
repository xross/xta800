package com.xmos.xta.engine.analysis.nodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.xmos.xta.engine.XtaInstruction;

public class Path extends Node {

	List<Node> m_nodes;
	
	public Path(List<Node> nodes) {
		super();
		assert(nodes!= null);
		assert(nodes.size()>1);
		m_nodes = nodes;
	}

	public Path(Path path) {
		super();
		m_nodes = path.m_nodes;
	}
	
	public List<XtaInstruction> getInstructions() {
		List<XtaInstruction> list = new LinkedList<XtaInstruction>();
		for(Node node : m_nodes){
			list.addAll(node.getInstructions());
		}
		return list;
	}

	public long getNumberCycles() {
		long cycles = 0;
		for(Node node : m_nodes){
			cycles += node.getNumberCycles();
		}
		return cycles;
	}

	public long getNumberFNOPS() {
		long fnops = 0;
		for(Node node : m_nodes){
			fnops += node.getNumberFNOPS();
		}
		return fnops;
	}

	public Set<Integer> getPCsCovered() {
		Set<Integer> pcs = new HashSet<Integer>();
		for(Node node : m_nodes){
			pcs.addAll(node.getPCsCovered());
		}
		return pcs;
	}

	public Node clone() {
		return new Path(this);
	}

	public Collection<Node> getContaintedNodes() {
		Collection<Node> containedNodes = new HashSet<Node> ();
		for(Node loop: m_nodes){
			containedNodes.addAll(loop.getContaintedNodes());
		}
		return containedNodes;
	}

	public String getGraphvizName() {
		return "cluster_Path" + m_id;
	}

	public void appendToGraph(StringBuffer body, StringBuffer connections,
			Collection<Node> last, Collection<Node> next, int indent, Node parent) {
		
		body.append(intToTab(indent) +"subgraph " + getGraphvizName()  + " {\n");
		body.append(intToTab(indent) +"style=filled;\n");
		body.append(intToTab(indent) +"fillcolor=pink;\n");
		body.append(intToTab(indent) +"color=black;\n");
		
		Collection<Node> lastThis = new HashSet<Node>(last);
		Collection<Node> nextThis = new HashSet<Node>();

		for (Node node : m_nodes) {
			node.appendToGraph(body, connections, lastThis, nextThis, indent+1, this);
			lastThis = nextThis;
			nextThis = new HashSet<Node>();
		}
		next.addAll(lastThis);
		
		body.append(intToTab(indent) +"}\n");
	}

	public void display(int indentLevel) {
		//printIndent(indentLevel);
		//System.out.printf("path(%d)\n", m_id);
		for (Node node : m_nodes) {
			node.display(indentLevel + 0);
		}
	}	
	public boolean isFunction() {
		return false;
	}

	public int getStartPc() {
		return m_nodes.get(0).getStartPc();
	}
}
