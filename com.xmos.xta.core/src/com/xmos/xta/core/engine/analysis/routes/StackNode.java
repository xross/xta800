package com.xmos.xta.core.engine.analysis.routes;

import java.util.List;
import java.util.Stack;

import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionReferenceNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;

public class StackNode implements Comparable<StackNode>{
	private List<Stack<FunctionReferenceNode>> m_nodeStacks;
	private InstructionNode m_instruction;
	public StackNode(InstructionNode instruction, List<Stack<FunctionReferenceNode>> stacks){
		m_nodeStacks = stacks;
		m_instruction = instruction;
	}

	public List<Stack<FunctionReferenceNode>> getNodeStacks(){
		return m_nodeStacks;
	}

	public InstructionNode getInstruction(){
		return m_instruction;
	}

	public String toString(){
		StringBuffer stacks = new StringBuffer();
		for(Stack<FunctionReferenceNode> stack : m_nodeStacks){
			stacks.append("\t");
			stacks.append(stack);
			stacks.append("\n");
		}
		return m_instruction + " from " + m_nodeStacks.size() + " stacks\n" + stacks.toString();
	}

	public int compareTo(StackNode o) { 
		return this.hashCode() - o.hashCode();
	}
}
