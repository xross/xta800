package com.xmos.xta.core.engine.analysis.structural.nodes.unknowns;

import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.UnknownFactory.Type;
import com.xmos.xta.core.engine.references.Reference;

public abstract class Unknown  {

    public enum State {
        UNKNOWN,
        KNOWN,
    }

    public enum Level {
        WARNING,
        ERROR,
    }

    public enum Source {
        NONE,
        ADD,
        SET,
        BINARY,
    }

    private Route m_route;
    private Node m_node;
    private Type m_type;
    private State m_state;
    private Source m_source;
    private Reference[] m_references;

    public abstract String getDescription();
    public abstract String getScriptStr(String routeIdStr, String[] refStrs);
    public abstract String getValueStr();

    public Unknown(Route route, Node node, Type type, State state, Source source, Reference[] references) {
        initialise(route, node, type, state, source, references);
    }

    public Route getRoute() {
        return m_route;
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
        case EXCLUSION:
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

    private void initialise(Route route, Node node, Type type, State state, Source source, Reference[] references) {
        m_route = route;
        m_node = node;
        m_type = type;
        m_state = state;
        m_source = source;
        m_references = references;
    }
}
