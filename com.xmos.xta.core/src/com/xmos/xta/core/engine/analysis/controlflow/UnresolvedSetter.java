package com.xmos.xta.core.engine.analysis.controlflow;

import java.util.Set;

import com.xmos.libs.util.IteratableSet;
import com.xmos.libs.util.NonIteratableSet;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.NullNode;

public class UnresolvedSetter {
	private Set<InstructionNode> m_functionStartNodes;

	public UnresolvedSetter(Set<InstructionNode> functionStartNodes) {
		m_functionStartNodes = functionStartNodes;
	}

	public void process() {
		for(InstructionNode inst : m_functionStartNodes){
			Set<Node> seen = new NonIteratableSet<Node>();
			findUnresolved(inst, seen);
		}
	}

	private void findUnresolved(Node inst, Set<Node> seen) {
		Set<Node> following = new IteratableSet<Node>();
		following.add(inst);
		while(!following.isEmpty()){
			Node current = following.iterator().next();
			
			seen.add(current);
			boolean isBranch = isBranch(current);
			int nonTerminatingNextInstructionCount = countNext(current);
			if(nonTerminatingNextInstructionCount == 0 && isBranch){
				current.setUnresolved(true);
			} else {
				if(current instanceof InstructionNode){
					InstructionNode i = (InstructionNode)current;
					if(i.getXtaInstruction().isLink()) {
						following.addAll(i.getBranchReturnNodes());
					}
				}
				
				for(Node next : current.getNextNodes()){
					if(!seen.contains(next)){
						following.add(next);
					}
				}
			}
			following.remove(current);
		}
	}

	private int countNext(Node current) {
		int count = 0;
		for(Node next : current.getNextNodes()){
			if(!(next instanceof NullNode)){
				count++;
			} else {
				if(current instanceof InstructionNode){
					InstructionNode inst = (InstructionNode)current;
					if(inst.getXtaInstruction().getReturn()){
						count++;
					}
				}
			}
		}
		return count;
	}

	private boolean isBranch(Node current) {
		if(current instanceof InstructionNode){
			InstructionNode inst = (InstructionNode)current;
			return inst.getXtaInstruction().isBranch() && !inst.getXtaInstruction().isLink() && !inst.getXtaInstruction().getReturn();
		}
		return false;
	}
}
