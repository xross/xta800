package com.xmos.xta.core.engine.analysis.controlflows;

import java.util.Set;

import com.xmos.libs.util.NonIteratableSet;
import com.xmos.xta.core.engine.analysis.controlflows.nodes.CfgInstructionNode;
import com.xmos.xta.core.engine.analysis.controlflows.nodes.CfgNode;
import com.xmos.xta.core.engine.analysis.controlflows.nodes.CfgNullNode;

class TerminatorAdder {
	private Set<CfgInstructionNode> m_functionStartNodes;

	TerminatorAdder(Set<CfgInstructionNode> functionStartNodes){
		m_functionStartNodes = functionStartNodes;
	}

	void process() {
		for(CfgInstructionNode inst : m_functionStartNodes){
			Set<CfgNode> terminals = new NonIteratableSet<CfgNode>();
			Set<CfgNode> seen = new NonIteratableSet<CfgNode>();
			findEndPoints(inst, seen, terminals);
			CfgNullNode nullNode = null;
			for(CfgNode node : terminals){
				if(node instanceof CfgNullNode){
					nullNode = (CfgNullNode) node;
					terminals.remove(node);
					break;
				}
			}
			if(nullNode == null) nullNode = new CfgNullNode();
			for(CfgNode terminal : terminals){
				CfgNode.connect(nullNode, terminal);
			}
		}
	}

	private void findEndPoints(CfgNode inst, Set<CfgNode> seen, Set<CfgNode> terminals) {
		Set<CfgNode> following = new NonIteratableSet<CfgNode>();
		following.add(inst);
		while(!following.isEmpty()){
			CfgNode current = following.iterator().next();
			seen.add(current);
			if(current.getNextNodesSize() == 0){
				terminals.add(current);
			} else {
				for(CfgNode next : current.getNextNodes()){
					if(!seen.contains(next)){
						following.add(next);
					}
				}
			}
			following.remove(current);
		}
	}
}
