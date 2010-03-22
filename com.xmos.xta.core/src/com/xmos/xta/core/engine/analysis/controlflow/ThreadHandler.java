package com.xmos.xta.core.engine.analysis.controlflow;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.xmos.libs.util.NonIteratableMap;
import com.xmos.libs.util.NonIteratableSet;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.analysis.binary.InstructionDecoder;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.NullNode;
import com.xmos.xta.core.engine.analysis.structural.patterns.CommonMethods;

public class ThreadHandler {
	private InstructionDecoder m_instDecoder;
	private Notifier m_notifier;

	public ThreadHandler(InstructionDecoder instDecoder, Notifier notifier) {
		m_instDecoder = instDecoder;
		m_notifier = notifier;
	}


	public Set<InstructionNode> getThreadNodes(CommandSource src, Collection<Long> threadPcs, String type) {
		Set<InstructionNode> nodes = new NonIteratableSet<InstructionNode>();
		for (long pc : threadPcs) {
			InstructionNode threadNode = m_instDecoder.getNode(pc);
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


	public Set<InstructionNode> ensureThreadStartExists(CommandSource src, Set<InstructionNode> threadStarts) {
		if (threadStarts == null  || threadStarts.isEmpty()) {
			m_notifier.notify(src, Notifier.NOTIFY_WARNING,
					"no thread starts found, assuming master thread starts at 0x10000");
			InstructionNode threadNode = m_instDecoder.getNode(0x10000);
			if (threadNode != null) {
				threadStarts.add(threadNode);
			} else {
				m_notifier.notify(src, Notifier.NOTIFY_WARNING,
						"failed to find thread start at PC 0x" + Integer.toHexString(0x10000));
			}
		}
		return threadStarts;
	}

	public Map<Long, Set<Node>> addTerminators(Collection<InstructionNode> threadStarts) {
		Map<Long, Set<Node>> threadToEndPoints = new NonIteratableMap<Long, Set<Node>>();
		for (InstructionNode threadStart : threadStarts) {
			NonIteratableSet<Node> terminals  = new NonIteratableSet<Node>();
			findTerminals(threadStart, new NonIteratableSet<Node>(), terminals);
			NullNode nullNode = new NullNode();
			threadToEndPoints.put(threadStart.getStartPc(), terminals);
			int nullNodeCount = 0;
			int count = 0;
			for (Node terminal : terminals) {
				if (!(terminal instanceof NullNode)) {
					count ++;
				} else {
					nullNode = (NullNode) terminal;
					nullNodeCount++;
				}
			}
			if (nullNodeCount == 1) {
				if (count > 0) {
					for (Node terminal : terminals) {
						if (!(terminal instanceof NullNode)) {
							CommonMethods.connect(nullNode, terminal);
						}
					}
				}
			} else {
				if (count > 1) {
					for (Node terminal : terminals) {
						if (!(terminal instanceof NullNode)) {
							CommonMethods.connect(nullNode, terminal);
						}
					}
				}
			}
		}
		return threadToEndPoints;
	}

	private void findTerminals(Node node, Set<Node> seen, Set<Node> terminals) {
		seen.add(node);

		if (node.getNextNodesSize() == 0) {
			assert(!terminals.contains(node));
			terminals.add(node);
		}

		for (Node next : node.getNextNodes()) {
			if (!seen.contains(next)) {
				findTerminals(next, seen, terminals);
			}
		}
	}
}
