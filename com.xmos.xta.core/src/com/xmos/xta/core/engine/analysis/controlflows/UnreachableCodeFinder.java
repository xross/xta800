package com.xmos.xta.core.engine.analysis.controlflows;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.xmos.libs.util.NonIteratableSet;
import com.xmos.xta.core.engine.analysis.binary.InstructionDecoder;
import com.xmos.xta.core.engine.analysis.controlflows.nodes.CfgInstructionNode;
import com.xmos.xta.core.engine.analysis.controlflows.nodes.CfgNode;

class UnreachableCodeFinder {

	private Set<CfgNode> m_unreachable;

	UnreachableCodeFinder() {
		m_unreachable = new NonIteratableSet<CfgNode>();
	}

	/**
	 * This finds all the code that is unreachable and removes it from the graph.
	 * It needs removing from the graph so that functions
	 * and threads don't have invalid predecessors.
	 */
	void analyzeReachable(InstructionDecoder id, Set<CfgInstructionNode> functionStarts) {
		m_unreachable.clear();
		m_unreachable.addAll(id.getInstructions());
		Set<CfgNode> reachable = new NonIteratableSet<CfgNode>();
		for (CfgNode node : functionStarts){
			Set<CfgNode> seen = new NonIteratableSet<CfgNode>();
			explore(node, seen);
			reachable.addAll(seen);
		}
		m_unreachable.removeAll(reachable);

		for (CfgNode unreachable : m_unreachable) {
			for (CfgNode next : unreachable.getNextNodes()) {
				next.removePrevNode(unreachable);
			}

			unreachable.getNextNodes().clear();
		}
	}

	private void explore(CfgNode node, Collection<CfgNode> seen) {
		List<CfgNode> toDo = new ArrayList<CfgNode>();
		toDo.add(node);

		while (!toDo.isEmpty()) {
			CfgNode currentNode = toDo.get(0);
			if (!seen.contains(currentNode)) {
				seen.add(currentNode);
				for (CfgNode next : currentNode.getNextNodes()) {
					if (!seen.contains(next)) {
						toDo.add(next);
					}
				}
			}

			toDo.remove(currentNode);
		}
	}
}
