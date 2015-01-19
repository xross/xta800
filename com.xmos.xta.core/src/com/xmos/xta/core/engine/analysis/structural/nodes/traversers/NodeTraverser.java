package com.xmos.xta.core.engine.analysis.structural.nodes.traversers;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public interface NodeTraverser {
    // called on arrival at the Node.
    TraverseStatus foundBlockNode(Node node, Object clientData);
    TraverseStatus foundFunctionNode(Node node, Object clientData);
    TraverseStatus foundIssueNode(Node node, Object clientData);
    TraverseStatus foundSequenceNode(Node node, Object clientData);
    TraverseStatus foundLoopNode(Node node, Object clientData);
    TraverseStatus foundInfiniteLoopNode(Node node, Object clientData);
    TraverseStatus foundSelfLoopNode(Node node, Object clientData);
    TraverseStatus foundConditionalNode(Node node, Object clientData);
    TraverseStatus foundInfiniteSelfLoopNode(Node node, Object clientData);
    TraverseStatus foundFunctionReferenceNode(Node node, Object clientData);

    // called on departure from the Node.
    TraverseStatus foundBlockNodeExit(Node node, Object clientData);
    TraverseStatus foundFunctionNodeExit(Node node, Object clientData);
    TraverseStatus foundIssueNodeExit(Node node, Object clientData);
    TraverseStatus foundSequenceNodeExit(Node node, Object clientData);
    TraverseStatus foundLoopNodeExit(Node node, Object clientData);
    TraverseStatus foundInfiniteLoopNodeExit(Node node, Object clientData);
    TraverseStatus foundSelfLoopNodeExit(Node node, Object clientData);
    TraverseStatus foundConditionalNodeExit(Node node, Object clientData);
    TraverseStatus foundInfiniteSelfLoopNodeExit(Node node, Object clientData);
    TraverseStatus foundFunctionReferenceNodeExit(Node node, Object clientData);
}