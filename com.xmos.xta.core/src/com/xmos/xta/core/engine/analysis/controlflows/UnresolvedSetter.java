package com.xmos.xta.core.engine.analysis.controlflows;

import java.util.Set;

import com.xmos.libs.util.IteratableSet;
import com.xmos.libs.util.NonIteratableSet;
import com.xmos.xta.core.engine.analysis.controlflows.nodes.CfgInstructionNode;
import com.xmos.xta.core.engine.analysis.controlflows.nodes.CfgNode;
import com.xmos.xta.core.engine.analysis.controlflows.nodes.CfgNullNode;

class UnresolvedSetter {
	private Set<CfgInstructionNode> m_functionStartNodes;

	UnresolvedSetter(Set<CfgInstructionNode> functionStartNodes) {
		m_functionStartNodes = functionStartNodes;
	}

	void process() {
		for(CfgInstructionNode inst : m_functionStartNodes){
			Set<CfgNode> seen = new NonIteratableSet<CfgNode>();
			findUnresolved(inst, seen);
		}
	}

	private void findUnresolved(CfgNode inst, Set<CfgNode> seen) {
		Set<CfgNode> following = new IteratableSet<CfgNode>();
		following.add(inst);
		while (!following.isEmpty()) {
			CfgNode current = following.iterator().next();
			
			seen.add(current);
			boolean isBranch = isBranch(current);
			int nonTerminatingNextInstructionCount = countNext(current);
			if (nonTerminatingNextInstructionCount == 0 && isBranch) {
				current.setUnresolved(true);
				
			} else {				
				current.setUnresolved(false);
				
				if (current instanceof CfgInstructionNode) {
					CfgInstructionNode i = (CfgInstructionNode)current;
					if (i.getXtaInstruction().isLink()) {
						following.addAll(i.getBranchReturnNodes());
					}
				}
				
				for (CfgNode next : current.getNextNodes()) {
					if (!seen.contains(next)) {
						following.add(next);
					}
				}
			}
			following.remove(current);
		}
	}

	private int countNext(CfgNode current) {
		int count = 0;
		for (CfgNode next : current.getNextNodes()) {
			if (!(next instanceof CfgNullNode)) {
				count++;
			} else {
				if (current instanceof CfgInstructionNode) {
					CfgInstructionNode inst = (CfgInstructionNode)current;
					if (inst.getXtaInstruction().getReturn()) {
						count++;
					}
				}
			}
		}
		return count;
	}

	private boolean isBranch(CfgNode current) {
		if(current instanceof CfgInstructionNode){
			CfgInstructionNode inst = (CfgInstructionNode)current;
			return inst.getXtaInstruction().isBranch() && !inst.getXtaInstruction().isLink() && !inst.getXtaInstruction().getReturn();
		}
		return false;
	}
}
