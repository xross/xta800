package com.xmos.xta.engine;

import java.util.Collection;
import java.util.HashSet;

import com.xmos.xta.engine.analysis.nodes.Node;

public class ThreadHandeler {

	private Collection<Integer> m_threadStarts;
	private Collection<Integer> m_threadStops;
	private InstructionDecoder m_id;
	
	public ThreadHandeler(InstructionDecoder id, Collection<Integer> threadStarts, Collection<Integer> threadStops) {
		m_threadStarts = threadStarts;
		m_threadStops = threadStops;
		m_id = id;
	}

	public Collection<Node> getThreadStarts() {
		Collection<Node> starts = new HashSet<Node>();
		for(int pc : m_threadStarts){
			Node threadStart = m_id.getNode(pc);
			if(threadStart != null){
				starts.add(threadStart);
			} else {
				//TODO notify of the error
			}
		}
		return starts;
	}

	public Collection<Node> getThreadEnds() {
		Collection<Node> ends = new HashSet<Node>();
		for(int pc : m_threadStops){
			Node threadStop = m_id.getNode(pc);
			if(threadStop != null){
				ends.add(threadStop);
			} else {
				//TODO notify of the error
			}
		}
		return ends;
	}

}
