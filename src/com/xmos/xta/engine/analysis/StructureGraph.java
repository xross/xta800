package com.xmos.xta.engine.analysis;

import java.util.Collection;
import com.xmos.xta.engine.analysis.nodes.Node;

public class StructureGraph {

	private Collection<Node> m_functions;
	private Collection<Node> m_threads;
	
	public StructureGraph(Collection<Node> functions, Collection<Node> threads) {
		super();
		assert(threads.size() > 0);
		m_functions = functions;
		m_threads = threads;
	}

	public Collection<Node> getFunctions() {
		return m_functions;
	}

	public Collection<Node> getThreads() {
		return m_threads;
	}
	
}
