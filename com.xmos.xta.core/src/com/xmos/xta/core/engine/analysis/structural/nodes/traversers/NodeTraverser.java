package com.xmos.xta.core.engine.analysis.structural.nodes.traversers;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public interface NodeTraverser {
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
}