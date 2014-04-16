package com.xmos.xta.core.engine.analysis.structural.nodes.unknowns;

import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown.Source;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown.State;
import com.xmos.xta.core.engine.references.Reference;

public class UnknownFactory {
    public enum Type {
        LOOP_ITERATIONS,
        LOOP_PATH_ITERATIONS,
        LOOP_SCOPE,
        INSTRUCTION_TIME,
        PATH_TIME,
        FUNCTION_TIME,
        EXCLUSION,
        OTHER,
    }

    public static Unknown createUnknown(Route route, Node node, Type type, State state) {
        return create(route, node, type, state, Source.NONE, new Reference[]{});
    }

    public static Unknown createKnown(Route route, Node node, Type type,
            State state, Source source, Reference[] references) {

        return create(route, node, type, state, source, references);
    }

    private static Unknown create(Route route, Node node, Type type,
            State state, Source source, Reference[] references) {

        switch (type) {
        case LOOP_ITERATIONS:
            return new LoopUnknown(route, node, type, state, source, references);

        case LOOP_PATH_ITERATIONS:
            return new LoopPathUnknown(route, node, type, state, source, references);

        case LOOP_SCOPE:
            return new LoopScopeUnknown(route, node, type, state, source, references);

        case INSTRUCTION_TIME:
            return new InstructionTimeUnknown(route, node, type, state, source, references);

        case PATH_TIME:
            return new PathTimeUnknown(route, node, type, state, source, references);

        case FUNCTION_TIME:
            return new FunctionTimeUnknown(route, node, type, state, source, references);

        case EXCLUSION:
            return new ExclusionUnknown(route, node, type, state, source, references);
            
        case OTHER:
            break;
        }
        assert(false);
        return null;
    }
}
