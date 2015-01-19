package com.xmos.xta.core.engine.analysis.structural.nodes.traversers;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class DefaultNodeTraverser implements NodeTraverser {
    // default handling on arrival at the Node.
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

    // default handling on exit from the Node.
    public TraverseStatus foundBlockNodeExit(Node node, Object clientState) {
        return TraverseStatus.CONTINUE;
    }
    public TraverseStatus foundFunctionNodeExit(Node node, Object clientState) {
        return TraverseStatus.CONTINUE;
    }
    public TraverseStatus foundFunctionReferenceNodeExit(Node node, Object clientState) {
        return TraverseStatus.CONTINUE;
    }
    public TraverseStatus foundIssueNodeExit(Node node, Object clientState) {
        return TraverseStatus.CONTINUE;
    }
    public TraverseStatus foundSequenceNodeExit(Node node, Object clientState) {
        return TraverseStatus.CONTINUE;
    }
    public TraverseStatus foundLoopNodeExit(Node node, Object clientState) {
        return TraverseStatus.CONTINUE;
    }
    public TraverseStatus foundInfiniteLoopNodeExit(Node node, Object clientState) {
        return TraverseStatus.CONTINUE;
    }
    public TraverseStatus foundSelfLoopNodeExit(Node node, Object clientState) {
        return TraverseStatus.CONTINUE;
    }
    public TraverseStatus foundConditionalNodeExit(Node node, Object clientState) {
        return TraverseStatus.CONTINUE;
    }
    public TraverseStatus foundInfiniteSelfLoopNodeExit(Node node, Object clientState) {
        return TraverseStatus.CONTINUE;
    }

}
