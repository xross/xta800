package com.xmos.xta.core.engine;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionReferenceNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class FunctionContainer {

	private String m_functionName;
	private InstructionNode m_startInstruction;
	private Collection<Node> m_functionContents;
	
	public FunctionContainer(InstructionNode startOfFunction, Collection<Node> functionContents) {
		m_startInstruction = startOfFunction;
		m_functionContents = functionContents;
	}

	public InstructionNode getStartInstruction() {
		return m_startInstruction;
	}
	
	public Collection<Node> getFunctionContents() {
		return m_functionContents;
	}
	
	public Set<FunctionReferenceNode> getFunctionReferenceNodes() {
		Set<FunctionReferenceNode> fnRefs = new TreeSet<FunctionReferenceNode>();
		for(Node node : m_functionContents){
			if(node instanceof FunctionReferenceNode){
				fnRefs.add((FunctionReferenceNode) node);
			}
		}
		return fnRefs;
	}
	
	public void setFunctionName(String functionName){
		m_functionName = functionName;
	}
	
	public String getFunctionName(){
		if(m_functionName == null){
			return "unnamed_function";
		} else {
			return m_functionName;
		}
		
	}
	
	public void dumpGraph() {
		Set<Node> seen = new HashSet<Node>();
		Set<Node> todo = new HashSet<Node>();
		System.out.println("#from node " + m_startInstruction);
		todo.add(m_startInstruction);
		System.out.println("digraph g {");
		while (!todo.isEmpty()) {
			Node currentNode = todo.iterator().next();
			seen.add(currentNode);
			if (currentNode instanceof FunctionReferenceNode) {
				System.out.println("node" + currentNode.getId() + " [label=\""
						+ currentNode
						+ "\", fillcolor=red, style=filled, shape=box];");
			} else if (currentNode instanceof InstructionNode){
				
				System.out.println("node" + currentNode.getId() + " [label=\""
						+ ((InstructionNode)currentNode).getXtaInstruction()
						+ "\", fillcolor=lightblue, style=filled, shape=box];");
			} else {
				System.out.println("node" + currentNode.getId() + " [label=\""
						+ currentNode
						+ "\", style=filled, shape=box];");
			}
			for (Node next : currentNode.getNextNodes()) {
				System.out.println("node" + currentNode.getId() + " -> node"
						+ next.getId());
				if (!seen.contains(next)) {
					todo.add(next);
				}
			}
			todo.remove(currentNode);
		}
		System.out.println("}");
	}
}
