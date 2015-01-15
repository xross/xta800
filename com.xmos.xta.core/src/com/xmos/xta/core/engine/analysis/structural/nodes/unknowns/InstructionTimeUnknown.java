package com.xmos.xta.core.engine.analysis.structural.nodes.unknowns;

import com.xmos.libs.core.utils.Conversion;
import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.structural.nodes.IssueNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.UnknownFactory.Type;
import com.xmos.xta.core.engine.references.Reference;

public class InstructionTimeUnknown extends Unknown {
    InstructionTimeUnknown(Route route, Node node, Type type, State state, Source source, Reference[] references) {
        super(route, node, type, state, source, references);
    }

    public String getDescription() {
        assert(getNode() instanceof IssueNode);
        IssueNode issueNode = (IssueNode)getNode();
        switch(getState()) {
        case UNKNOWN:
            return String.format("Time taken for instruction (0x%x: %s) is unknown",
                    issueNode.getStartPc(),
                    issueNode.getMnemonic());

        case KNOWN:
            return String.format("Time for instruction (0x%x: %s) set to %s",
                    issueNode.getStartPc(),
                    issueNode.getMnemonic(),
                    Conversion.getTimeString(getNode().getAttributes(false).getFixedTime().getNs()));
        }
        assert(false);
        return "";
    }

    public String getScriptStr(String routeIdStr, String[] refStrs) {
        assert(getSource() == Source.SET);
        return String.format("set instructiontime %s %s %s",
                routeIdStr, refStrs[0],
                getNode().getAttributes(false).getFixedTimeString());
    }

    public String getValueStr() {
        return getNode().getAttributes(false).getFixedTimeString();
    }
}
