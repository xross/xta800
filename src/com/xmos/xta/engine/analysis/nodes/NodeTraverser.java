package com.xmos.xta.engine.analysis.nodes;

public interface NodeTraverser {
    boolean foundBlockNode(Node node, int depth);
    boolean foundFunctionNode(Node node, int depth);
    boolean foundInstructionNode(Node node, int depth);
    boolean foundPathNode(Node node, int depth);
    boolean foundLoopNode(Node node, int depth);
    boolean foundSectionNode(Node node, int depth);
    boolean foundTerminalNode(Node node, int depth);
}
