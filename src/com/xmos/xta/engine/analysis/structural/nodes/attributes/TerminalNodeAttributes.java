package com.xmos.xta.engine.analysis.structural.nodes.attributes;

import java.util.HashMap;
import java.util.Map;

import com.xmos.xta.engine.analysis.structural.nodes.Node;

public class TerminalNodeAttributes extends NodeAttributes {
	private Map<Node, Boolean> m_setOptions;

	public TerminalNodeAttributes(Node node) {
		super(node);
		m_setOptions = new HashMap<Node, Boolean>();
   		for (Node child : getNode().getChildren()) {
   			m_setOptions.put(child, false);
   		}		
	}

	public long getNumPaths() {
		long value = 0;
		for (Node child : getNode().getChildren()) {
			value += child.getAttributes().getNumPaths();
		}
		return value;
	}
	
	public Map<Node, Boolean> getSetOptions() {
		return m_setOptions;
	}		
}
