package com.xmos.xta.engine.analysis.nodes;

public interface NodeTraverser {
    void foundBlockNode(Node node, int depth);
    void foundFunctionNode(Node node, int depth);
    void foundInstructionNode(Node node, int depth);
    void foundPathNode(Node node, int depth);
    void foundLoopNode(Node node, int depth);
    void foundSectionNode(Node node, int depth);
    void foundTerminalNode(Node node, int depth);
}
