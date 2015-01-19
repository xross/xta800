package com.xmos.xta.core.engine.analysis.structural.nodes.traversers;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class DefaultNodeTraverser implements NodeTraverser {
    public TraverseStatus foundBlockNode(Node node, Object clientState) {
        return TraverseStatus.CONTINUE;
    }

    public TraverseStatus foundFunctionNode(Node node, Object clientState) {
        return TraverseStatus.CONTINUE;
    }

    public TraverseStatus foundFunctionReferenceNode(Node node, Object clientState) {
        return TraverseStatus.CONTINUE;
    }

    public TraverseStatus foundIssueNode(Node node, Object clientState) {
        return TraverseStatus.CONTINUE;
    }

    public TraverseStatus foundSequenceNode(Node node, Object clientState) {
        return TraverseStatus.CONTINUE;
    }

    public TraverseStatus foundLoopNode(Node node, Object clientState) {
        return TraverseStatus.CONTINUE;
    }

    public TraverseStatus foundInfiniteLoopNode(Node node, Object clientState) {
        return TraverseStatus.CONTINUE;
    }

    public TraverseStatus foundSelfLoopNode(Node node, Object clientState) {
        return TraverseStatus.CONTINUE;
    }

    public TraverseStatus foundConditionalNode(Node node, Object clientState) {
        return TraverseStatus.CONTINUE;
    }

    public TraverseStatus foundInfiniteSelfLoopNode(Node node, Object clientState) {
        return TraverseStatus.CONTINUE;
    }

}
