package com.xmos.xta.core.engine.analysis.controlflows;

import java.util.Collection;
import java.util.Set;

import com.xmos.libs.memorymonitor.MemoryMonitorException;
import com.xmos.libs.util.IteratableSet;
import com.xmos.libs.util.NonIteratableSet;
import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.Engine;
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
	
	private Set<Long> m_containedPcs;
	
	public FunctionContainer(InstructionNode startOfFunction, 
			Set<InstructionNode> functionInstructions,
			Set<FunctionReferenceNode> functionRefNodes) {
		m_startInstruction = startOfFunction;
		m_functionRefNodes = functionRefNodes;
		m_functionInstructions = functionInstructions;
		m_cachedFunction = null;
		m_functionName = "unnamed_function";
		m_containedPcs = new NonIteratableSet<Long>();
		
		for(InstructionNode inst: m_functionInstructions){
			m_containedPcs.add(inst.getPC());
		}
	}

	public InstructionNode getStartInstruction() {
		return m_startInstruction;
	}
	
	public Collection<Node> getFunctionContents() {
		Collection<Node> contents = new IteratableSet<Node>();
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
		return m_functionName;
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

	public Node getCachedFunction(Engine engine) 
	    throws AbortedException, MemoryMonitorException {
		
		if (m_cachedFunction == null){
			StructuralAnalysisEngine sae = new StructuralAnalysisEngine();
			m_cachedFunction = sae.makeFnContainerIntoStructure(this, engine);
		}
		return m_cachedFunction;
	}

	public int compareTo(FunctionContainer o) {
		return (int)Math.signum(this.getStartInstruction().getPC() - o.getStartInstruction().getPC());
	}

	public boolean containsPc(long pc) {
		return m_containedPcs.contains(pc);
	}
	
	public void dumpGraph(Console console) {
		Set<Node> seen = new NonIteratableSet<Node>();
		Set<Node> todo = new IteratableSet<Node>();
		
		console.display("#from node " + m_startInstruction + "\n");
		todo.add(m_startInstruction);
		console.display("digraph g {\n");
		
		while (!todo.isEmpty()) {
			Node currentNode = todo.iterator().next();
			seen.add(currentNode);
			if (currentNode instanceof FunctionReferenceNode) {
				console.display("node" + currentNode.getId() + " [label=\""
						+ currentNode
						+ "\", fillcolor=red, style=filled, shape=box];\n");
				
			} else if (currentNode instanceof InstructionNode){				
				console.display("node" + currentNode.getId() + " [label=\""
						+ ((InstructionNode)currentNode).getXtaInstruction()
						+ "\", fillcolor=lightblue, style=filled, shape=box];\n");
				
			} else {
				console.display("node" + currentNode.getId() + " [label=\""
						+ currentNode
						+ "\", style=filled, shape=box];\n");
				
			}
			for (Node next : currentNode.getNextNodes()) {
				console.display("node" + currentNode.getId() + " -> node"
						+ next.getId() + "\n");
				
				if (!seen.contains(next)) {
					todo.add(next);
				}
			}
			todo.remove(currentNode);
		}
		console.display("}\n");
	}	
}
