package com.xmos.xta.engine.analysis.controlflow;

import java.util.Collection;
import java.util.HashSet;
import java.util.TreeSet;

import com.xmos.xta.engine.Notifier;
import com.xmos.xta.engine.analysis.structural.nodes.Node;

public class ThreadHandler {
	private InstructionDecoder m_instDecoder;
	private Notifier m_notifier;
	
	public ThreadHandler(InstructionDecoder instDecoder, Notifier notifier) {
		m_instDecoder = instDecoder;
		m_notifier = notifier;
	}

	public Collection<Node> getThreadNodes(Collection<Integer> threadPcs) {
		Collection<Node> nodes = new TreeSet<Node>();
		for(int pc : threadPcs){
			Node threadNode = m_instDecoder.getNode(pc);
			if (threadNode != null){
				nodes.add(threadNode);
			} else {
				m_notifier.notify(Notifier.NOTIFY_WARNING,
						"Failed to find PC 0x" + Integer.toHexString(pc));
			}
		}
		return nodes;
	}
	
	public Collection<Node> ensureThreadStartExists(Collection<Node> threadStarts){
		
		
		if(threadStarts.isEmpty() || threadStarts == null){
			m_notifier.notify(Notifier.NOTIFY_WARNING,
					"No thread starts found, assuming master thread starts at 0x10000");
			Node threadNode = m_instDecoder.getNode(0x10000);
			if (threadNode != null){
				threadStarts.add(threadNode);
			} else {
				m_notifier.notify(Notifier.NOTIFY_WARNING,
						"Failed to find PC 0x" + Integer.toHexString(0x10000));
			}
		}
		
		return threadStarts;
	}
}
