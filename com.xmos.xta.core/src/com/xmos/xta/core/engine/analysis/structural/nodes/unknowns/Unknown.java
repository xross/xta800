package com.xmos.xta.core.engine.analysis.structural.nodes.unknowns;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.UnknownFactory.Type;
import com.xmos.xta.core.engine.references.Reference;

public abstract class Unknown implements Comparable<Unknown> {
	
	public enum State {
		UNKNOWN,
		KNOWN,
	}

	public enum Level {
		WARNING,
		ERROR,
	}

	public enum Source {
		USER,
		BINARY,
	}

	private Node m_node;
	private Type m_type;
	private State m_state;
    private Source m_source;
    private Reference[] m_references;

    public abstract String getDescription();
    public abstract String getScriptStr(String routeIdStr, String[] refStrs) throws Exception;
    
	public Unknown(Node node, Type type, State state, Source source, Reference[] references) {
		initialise(node, type, state, source, references);
	}

	public Node getNode() {
		return m_node;
	}

	public Type getType() {
		return m_type;
	}

	public Level getLevel() {
		switch (m_type) {
		case LOOP_ITERATIONS:
    		return Level.ERROR;
		case LOOP_PATH_ITERATIONS:
		case LOOP_SCOPE:
		case INSTRUCTION_TIME:
		case PATH_TIME:
		case FUNCTION_TIME:
			return Level.WARNING;
		}
		assert(false);
		return Level.ERROR;
	}

	public State getState() {
		return m_state;
	}

	public Source getSource() {
		return m_source;
	}
	
	public Reference[] getReferences() {
		return m_references;
	}

	public void setState(State state) {
		m_state = state;
	}

	private void initialise(Node node, Type type, State state, Source source, Reference[] references) {
		m_node = node;
		m_type = type;		
		m_state = state;
		m_source = source;
		m_references = references;
	}
	
	public int compareTo(Unknown o) {
		int value = m_node.compareTo(o.m_node);
		if (value != 0)
			return value;
		
		value = m_type.compareTo(o.m_type);
		if (value != 0)
			return value;
		
		value = m_state.compareTo(o.m_state);
		if (value != 0)
			return value;

		value = Integer.signum(m_references.length - o.m_references.length);
		if (value != 0)
			return value;
		
		for (int i = 0; i < m_references.length; i++) {
			value = m_references[i].compareTo(o.m_references[i]);
			if (value != 0) {
				return value;
			}
		}

		return 0;
	}
}
