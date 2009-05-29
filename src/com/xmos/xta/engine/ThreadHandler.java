package com.xmos.xta.engine;

import java.util.Collection;
import java.util.HashSet;

import com.xmos.xta.engine.analysis.nodes.Node;

public class ThreadHandler {
	private InstructionDecoder m_id;
	private Notifier m_notifier;
	
	public ThreadHandler(InstructionDecoder id, Notifier notifier) {
		m_id = id;
		m_notifier = notifier;
	}

	public Collection<Node> getThreadNodes(Collection<Integer> threadPcs) {
		Collection<Node> nodes = new HashSet<Node>();
		for(int pc : threadPcs){
			Node threadNode = m_id.getNode(pc);
			if (threadNode != null){
				nodes.add(threadNode);
			} else {
				m_notifier.notify(Notifier.NOTIFY_WARNING,
						"Failed to find PC 0x" + Integer.toHexString(pc));
			}
		}
		return nodes;
	}
}
