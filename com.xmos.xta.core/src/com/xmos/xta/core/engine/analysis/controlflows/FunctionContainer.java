package com.xmos.xta.core.engine.analysis.controlflows;

import java.util.Collection;
import java.util.Set;

import com.xmos.libs.util.IteratableSet;
import com.xmos.libs.util.NonIteratableSet;
import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.engine.analysis.controlflows.nodes.CfgFunctionReferenceNode;
import com.xmos.xta.core.engine.analysis.controlflows.nodes.CfgInstructionNode;
import com.xmos.xta.core.engine.analysis.controlflows.nodes.CfgNode;

public class FunctionContainer implements Comparable<FunctionContainer> {

	private String m_functionName;
	private CfgInstructionNode m_startInstruction;
	private Set<CfgInstructionNode> m_functionInstructions;
	private Set<CfgFunctionReferenceNode> m_functionRefNodes;
	private Set<Long> m_containedPcs;
	
	FunctionContainer(CfgInstructionNode startInstruction, 
			Set<CfgInstructionNode> functionInstructions,
			Set<CfgFunctionReferenceNode> functionRefNodes) {
		m_startInstruction = startInstruction;
		m_functionRefNodes = functionRefNodes;
		m_functionInstructions = functionInstructions;
		m_functionName = "unnamed_function";
		m_containedPcs = new NonIteratableSet<Long>();
		
		for (CfgInstructionNode inst: m_functionInstructions) {
			m_containedPcs.add(inst.getPc());
		}
	}

	public CfgInstructionNode getStartInstruction() {
		return m_startInstruction;
	}
	
	public Collection<CfgNode> getFunctionContents() {
		Collection<CfgNode> contents = new IteratableSet<CfgNode>();
		contents.addAll(m_functionInstructions);
		contents.addAll(m_functionRefNodes);
		return contents;
	}

	public Set<CfgFunctionReferenceNode> getFunctionReferenceNodes() {
		return m_functionRefNodes;
	}
	
	public Set<CfgInstructionNode> getFunctionInstructionNodes() {
		return m_functionInstructions;
	}
	
	public void setFunctionName(String functionName){
		m_functionName = functionName;
	}
	
	public String getFunctionName(){
		return m_functionName;
	}

	public CfgInstructionNode getInstruction(long pc) {
		for (CfgInstructionNode inst : m_functionInstructions) {
			if (inst.getPc() == pc) {
				return inst;
			}
		}
		assert(false);
		return null;
	}

	public int compareTo(FunctionContainer o) {
		return (int)Math.signum(this.getStartInstruction().getPc() - o.getStartInstruction().getPc());
	}

	public boolean containsPc(long pc) {
		return m_containedPcs.contains(pc);
	}
	
	public void dumpControlFlow(Console console) {
		Set<CfgNode> seen = new NonIteratableSet<CfgNode>();
		Set<CfgNode> todo = new IteratableSet<CfgNode>();
		
		console.display("#from node " + m_startInstruction + "\n");
		todo.add(m_startInstruction);
		console.display("digraph g {\n");
		
		while (!todo.isEmpty()) {
			CfgNode currentNode = todo.iterator().next();
			seen.add(currentNode);
			if (currentNode instanceof CfgFunctionReferenceNode) {
				console.display("node" + currentNode.getId() + " [label=\""
						+ currentNode
						+ "\", fillcolor=red, style=filled, shape=box];\n");
				
			} else if (currentNode instanceof CfgInstructionNode){				
				console.display("node" + currentNode.getId() + " [label=\""
						+ ((CfgInstructionNode)currentNode).getXtaInstruction()
						+ "\", fillcolor=lightblue, style=filled, shape=box];\n");
				
			} else {
				console.display("node" + currentNode.getId() + " [label=\""
						+ currentNode
						+ "\", style=filled, shape=box];\n");
				
			}
			for (CfgNode next : currentNode.getNextNodes()) {
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
	
	public Set<CfgNode> getControlFlow() {
		Set<CfgNode> seen = new IteratableSet<CfgNode>();
		Set<CfgNode> todo = new IteratableSet<CfgNode>();
		
		todo.add(m_startInstruction);
		while (!todo.isEmpty()) {
			CfgNode currentNode = todo.iterator().next();
			seen.add(currentNode);						
			for (CfgNode next : currentNode.getNextNodes()) {
				if (!seen.contains(next)) {
					todo.add(next);
				}
			}
			todo.remove(currentNode);
		}
		return seen;
	}	
}
