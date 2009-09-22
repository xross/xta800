package com.xmos.xta.core.engine.analysis.structural.nodes.traversers;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public interface NodeTraverser {
    boolean foundBlockNode(Node node, int depth);
    boolean foundFunctionNode(Node node, int depth);
    boolean foundInstructionNode(Node node, int depth);
    boolean foundSequenceNode(Node node, int depth);
    boolean foundLoopNode(Node node, int depth);
    boolean foundInfiniteLoopNode(Node node, int depth);
    boolean foundSelfLoopNode(Node node, int depth);
    boolean foundConditionalNode(Node node, int depth);
    boolean foundInfiniteSelfLoopNode(Node node, int depth);
}
