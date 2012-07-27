package com.xmos.xta.core.engine.analysis.routes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

import com.xmos.libs.core.controlflow.nodes.CfgFunctionReferenceNode;
import com.xmos.libs.core.controlflow.nodes.CfgInstructionNode;

public class StackNode implements Comparable<StackNode>{
	private List<Stack<CfgFunctionReferenceNode>> m_nodeStacks;
	private CfgInstructionNode m_instruction;
	
	public StackNode(CfgInstructionNode instruction, List<Stack<CfgFunctionReferenceNode>> stacks){
		m_nodeStacks = stacks;
		m_instruction = instruction;
	}

	public List<Stack<CfgFunctionReferenceNode>> getNodeStacks(){
		return m_nodeStacks;
	}

	public CfgInstructionNode getInstruction(){
		return m_instruction;
	}

	public String toString(){
		StringBuffer stacks = new StringBuffer();
		for(Stack<CfgFunctionReferenceNode> stack : m_nodeStacks){
			stacks.append("\t");
			stacks.append(stack);
			stacks.append("\n");
		}
		return m_instruction + " from " + m_nodeStacks.size() + " stacks\n" + stacks.toString();
	}

	public int compareTo(StackNode o) { 
		return this.hashCode() - o.hashCode();
	}
	
	public Collection<String> getBackTrails() {
		Collection<String> backTrails = new ArrayList<String>();

		for (Stack<CfgFunctionReferenceNode> stack : m_nodeStacks) {
			String backTrail = "";			
			for (CfgFunctionReferenceNode node : stack) {
				
				long callPc = node.getFunctionCallPc();
				if (callPc != 0) {
					backTrail += String.format("CALL:0x%x,", callPc);					
				}
			}
			backTrail += String.format("0x%x", m_instruction.getPc());
			backTrails.add(backTrail);
		}
		return backTrails;
	}
}
