package com.xmos.xta.core.engine.analysis.structural.nodes.traversers;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public abstract class UntypedNodeTraverser implements NodeTraverser {

    public abstract TraverseStatus foundNode(Node node, Object clientState);

    public TraverseStatus foundNodeExit(Node node, Object clientState) {
        return TraverseStatus.CONTINUE;
    }

    // default handling on arrival at the Node.
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

    // default handling on exit from the Node.
    public TraverseStatus foundBlockNodeExit(Node node, Object clientState) {
        return foundNodeExit(node, clientState);
    }
    public TraverseStatus foundFunctionNodeExit(Node node, Object clientState) {
        return foundNodeExit(node, clientState);
    }
    public TraverseStatus foundFunctionReferenceNodeExit(Node node, Object clientState) {
        return foundNodeExit(node, clientState);
    }
    public TraverseStatus foundIssueNodeExit(Node node, Object clientState) {
        return foundNodeExit(node, clientState);
    }
    public TraverseStatus foundSequenceNodeExit(Node node, Object clientState) {
        return foundNodeExit(node, clientState);
    }
    public TraverseStatus foundLoopNodeExit(Node node, Object clientState) {
        return foundNodeExit(node, clientState);
    }
    public TraverseStatus foundInfiniteLoopNodeExit(Node node, Object clientState) {
        return foundNodeExit(node, clientState);
    }
    public TraverseStatus foundSelfLoopNodeExit(Node node, Object clientState) {
        return foundNodeExit(node, clientState);
    }
    public TraverseStatus foundInfiniteSelfLoopNodeExit(Node node, Object clientState) {
        return foundNodeExit(node, clientState);
    }
    public TraverseStatus foundConditionalNodeExit(Node node, Object clientState) {
        return foundNodeExit(node, clientState);
    }

}
