package com.xmos.xta.core.engine.analysis.structural.nodes.traversers;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public abstract class UntypedNodeTraverser implements NodeTraverser {
    public abstract TraverseStatus foundNode(Node node, Object clientState);

    public TraverseStatus foundBlockNode(Node node, Object clientState) {
        return foundNode(node, clientState);
    }

    public TraverseStatus foundFunctionNode(Node node, Object clientState) {
        return foundNode(node, clientState);
    }

    public TraverseStatus foundFunctionReferenceNode(Node node, Object clientState) {
        return foundNode(node, clientState);
    }

    public TraverseStatus foundIssueNode(Node node, Object clientState) {
        return foundNode(node, clientState);
    }

    public TraverseStatus foundSequenceNode(Node node, Object clientState) {
        return foundNode(node, clientState);
    }

    public TraverseStatus foundLoopNode(Node node, Object clientState) {
        return foundNode(node, clientState);
    }

    public TraverseStatus foundInfiniteLoopNode(Node node, Object clientState) {
        return foundNode(node, clientState);
    }

    public TraverseStatus foundSelfLoopNode(Node node, Object clientState) {
        return foundNode(node, clientState);
    }

    public TraverseStatus foundInfiniteSelfLoopNode(Node node, Object clientState) {
        return foundNode(node, clientState);
    }

    public TraverseStatus foundConditionalNode(Node node, Object clientState) {
        return foundNode(node, clientState);
    }
}
