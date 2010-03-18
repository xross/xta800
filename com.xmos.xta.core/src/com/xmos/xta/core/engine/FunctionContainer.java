package com.xmos.xta.core.engine;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import com.xmos.xta.core.engine.analysis.structural.StructuralAnalysisEngine;
import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionReferenceNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class FunctionContainer implements Comparable<FunctionContainer> {

	private String m_functionName;
	private InstructionNode m_startInstruction;
	private Set<InstructionNode> m_functionInstructions;
	private Set<FunctionReferenceNode> m_functionRefNodes;
	private Node m_cachedFunction;
	
	public FunctionContainer(InstructionNode startOfFunction, 
			Set<InstructionNode> functionInstructions,
			Set<FunctionReferenceNode> functionRefNodes) {
		m_startInstruction = startOfFunction;
		m_functionRefNodes = functionRefNodes;
		m_functionInstructions = functionInstructions;
		m_cachedFunction = null;
		m_functionName = "unnamed_function";
	}

	public InstructionNode getStartInstruction() {
		return m_startInstruction;
	}
	
	public Collection<Node> getFunctionContents() {
		Collection<Node> contents = new TreeSet<Node>();
		contents.addAll(m_functionInstructions);
		contents.addAll(m_functionRefNodes);
		return contents;
	}
	
	public Set<FunctionReferenceNode> getFunctionReferenceNodes() {
		return m_functionRefNodes;
	}
	
	public Set<InstructionNode> getFunctionInstructionNodes() {
		return m_functionInstructions;
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

	public InstructionNode getInstruction(long pc) {
		for(InstructionNode inst : m_functionInstructions){
			if(inst.getPC() == pc){
				return inst;
			}
		}
		assert(false);
		return null;
	}

	public Node getCachedFunction(Engine engine) {
		if(m_cachedFunction == null){
			StructuralAnalysisEngine sae = new StructuralAnalysisEngine();
			m_cachedFunction = sae.makeFnContainerIntoStructure(this, engine);
		}
		return m_cachedFunction;
	}

	public int compareTo(FunctionContainer o) {
		return (int)Math.signum(this.getStartInstruction().getPC() - o.getStartInstruction().getPC());
	}
}
