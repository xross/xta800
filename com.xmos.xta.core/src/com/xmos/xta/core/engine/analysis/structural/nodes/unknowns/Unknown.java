package com.xmos.xta.core.engine.analysis.structural.nodes.unknowns;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.UnknownFactory.Type;
import com.xmos.xta.core.engine.references.Reference;

public abstract class Unknown {
	public enum State {
		UNKNOWN,
		KNOWN,
	}

	public enum Source {
		EXPLICIT,
		IMPLICIT,
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
}
