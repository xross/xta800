package com.xmos.xta.core.engine.analysis.structural.nodes.unknowns;

import com.xmos.libs.core.utils.Conversion;
import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.UnknownFactory.Type;
import com.xmos.xta.core.engine.references.Reference;

public class FunctionTimeUnknown extends Unknown {
    FunctionTimeUnknown(Route route, Node node, Type type, State state, Source source, Reference[] references) {
        super(route, node, type, state, source, references);
    }

    public String getDescription() {
        assert(getState() == State.KNOWN);
        return String.format("Time taken for function (node id: %d) is set to %s",
                getNode().getId(),
                Conversion.getTimeString(getNode().getAttributes(false).getFixedTime().getNs()));
    }

    public String getScriptStr(String routeIdStr, String[] refStrs) {
        assert(getSource() == Source.SET);
        return String.format("set functiontime %s %s %s",
                routeIdStr, refStrs[0],
                getNode().getAttributes(false).getFixedTimeString());
    }

    public String getValueStr() {
        return getNode().getAttributes(false).getFixedTimeString();
    }
}
