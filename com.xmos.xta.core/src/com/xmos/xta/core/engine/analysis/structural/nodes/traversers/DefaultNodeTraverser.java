package com.xmos.xta.core.engine.analysis.structural.nodes.traversers;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class DefaultNodeTraverser implements NodeTraverser {
    public TraverseStatus foundBlockNode(Node node) {
        return TraverseStatus.CONTINUE;
    }

    public TraverseStatus foundFunctionNode(Node node) {
        return TraverseStatus.CONTINUE;
    }

    public TraverseStatus foundFunctionReferenceNode(Node node) {
        return TraverseStatus.CONTINUE;
    }

    public TraverseStatus foundInstructionNode(Node node) {
        return TraverseStatus.CONTINUE;
    }

    public TraverseStatus foundSequenceNode(Node node) {
        return TraverseStatus.CONTINUE;
    }

    public TraverseStatus foundLoopNode(Node node) {
        return TraverseStatus.CONTINUE;
    }

    public TraverseStatus foundInfiniteLoopNode(Node node) {
        return TraverseStatus.CONTINUE;
    }

    public TraverseStatus foundSelfLoopNode(Node node) {
        return TraverseStatus.CONTINUE;
    }

    public TraverseStatus foundConditionalNode(Node node) {
        return TraverseStatus.CONTINUE;
    }

    public TraverseStatus foundInfiniteSelfLoopNode(Node node) {
        return TraverseStatus.CONTINUE;
    }

}
