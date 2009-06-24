package com.xmos.xta.engine.analysis.controlflow;

import java.util.Collection;
import java.util.TreeSet;

import com.xmos.xta.engine.Notifier;
import com.xmos.xta.engine.analysis.structural.nodes.InstructionNode;

public class ThreadHandler {
	private InstructionDecoder m_instDecoder;
	private Notifier m_notifier;
	
	public ThreadHandler(InstructionDecoder instDecoder, Notifier notifier) {
		m_instDecoder = instDecoder;
		m_notifier = notifier;
	}

	public Collection<InstructionNode> getThreadNodes(Collection<Integer> threadPcs) {
		Collection<InstructionNode> nodes = new TreeSet<InstructionNode>();
		for(int pc : threadPcs){
			InstructionNode threadNode = m_instDecoder.getNode(pc);
			if (threadNode != null){
				nodes.add(threadNode);
			} else {
				m_notifier.notify(Notifier.NOTIFY_WARNING,
						"Failed to find PC 0x" + Integer.toHexString(pc));
			}
		}
		return nodes;
	}
	
	public Collection<InstructionNode> ensureThreadStartExists(Collection<InstructionNode> threadStarts){
		
		
		if(threadStarts.isEmpty() || threadStarts == null){
			m_notifier.notify(Notifier.NOTIFY_WARNING,
					"No thread starts found, assuming master thread starts at 0x10000");
			InstructionNode threadNode = m_instDecoder.getNode(0x10000);
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
