package com.xmos.xta.core.engine.analysis.controlflows;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.xmos.libs.util.NonIteratableMap;
import com.xmos.libs.util.NonIteratableSet;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.analysis.binary.InstructionDecoder;
import com.xmos.xta.core.engine.analysis.controlflows.nodes.CfgInstructionNode;
import com.xmos.xta.core.engine.analysis.controlflows.nodes.CfgNode;
import com.xmos.xta.core.engine.analysis.controlflows.nodes.CfgNullNode;

class ThreadHandler {
	private InstructionDecoder m_instDecoder;
	private Notifier m_notifier;

	ThreadHandler(InstructionDecoder instDecoder, Notifier notifier) {
		m_instDecoder = instDecoder;
		m_notifier = notifier;
	}

	Set<CfgInstructionNode> getThreadNodes(CommandSource src, Collection<Long> threadPcs, String type) {
		Set<CfgInstructionNode> nodes = new NonIteratableSet<CfgInstructionNode>();
		for (long pc : threadPcs) {
			CfgInstructionNode threadNode = m_instDecoder.getNode(pc);
			if (threadNode != null) {
				nodes.add(threadNode);
				threadNode.setUnresolved(true);
			} else {
				m_notifier.notify(src, Notifier.NOTIFY_WARNING,
						"failed to find " + type + " at PC 0x" + Long.toHexString(pc));
			}
		}
		return nodes;
	}

	Set<CfgInstructionNode> ensureThreadStartExists(CommandSource src, Set<CfgInstructionNode> threadStarts) {
		if (threadStarts == null  || threadStarts.isEmpty()) {
			m_notifier.notify(src, Notifier.NOTIFY_WARNING,
					"no thread starts found, assuming master thread starts at 0x10000");
			CfgInstructionNode threadNode = m_instDecoder.getNode(0x10000);
			if (threadNode != null) {
				threadStarts.add(threadNode);
			} else {
				m_notifier.notify(src, Notifier.NOTIFY_WARNING,
						"failed to find thread start at PC 0x" + Integer.toHexString(0x10000));
			}
		}
		return threadStarts;
	}

	Map<Long, Set<CfgNode>> addTerminators(Collection<CfgInstructionNode> threadStarts) {
		Map<Long, Set<CfgNode>> threadToEndPoints = new NonIteratableMap<Long, Set<CfgNode>>();
		for (CfgInstructionNode threadStart : threadStarts) {
			NonIteratableSet<CfgNode> terminals  = new NonIteratableSet<CfgNode>();
			findTerminals(threadStart, new NonIteratableSet<CfgNode>(), terminals);
			CfgNullNode nullNode = new CfgNullNode();
			threadToEndPoints.put(threadStart.getPc(), terminals);
			int nullNodeCount = 0;
			int count = 0;
			for (CfgNode terminal : terminals) {
				if (!(terminal instanceof CfgNullNode)) {
					count ++;
				} else {
					nullNode = (CfgNullNode) terminal;
					nullNodeCount++;
				}
			}
			if (nullNodeCount == 1) {
				if (count > 0) {
					for (CfgNode terminal : terminals) {
						if (!(terminal instanceof CfgNullNode)) {
							CfgNode.connect(nullNode, terminal);
						}
					}
				}
			} else {
				if (count > 1) {
					for (CfgNode terminal : terminals) {
						if (!(terminal instanceof CfgNullNode)) {
							CfgNode.connect(nullNode, terminal);
						}
					}
				}
			}
		}
		return threadToEndPoints;
	}

	private void findTerminals(CfgNode node, Set<CfgNode> seen, Set<CfgNode> terminals) {
		seen.add(node);

		if (node.getNextNodesSize() == 0) {
			assert(!terminals.contains(node));
			terminals.add(node);
		}

		for (CfgNode next : node.getNextNodes()) {
			if (!seen.contains(next)) {
				findTerminals(next, seen, terminals);
			}
		}
	}
}
