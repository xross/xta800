package com.xmos.xta.engine.analysis.nodes;

public interface NodeTraverser {
    void foundBlockNode(BlockNode node, int depth);
    void foundFunctionNode(FunctionNode node, int depth);
    void foundInstructionNode(InstructionNode node, int depth);
    void foundLoopNode(LoopNode node, int depth);
    void foundSectionNode(SectionNode node, int depth);
    void foundTerminalNode(TerminalNode node, int depth);
}
