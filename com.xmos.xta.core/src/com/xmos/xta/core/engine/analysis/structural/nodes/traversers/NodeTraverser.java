package com.xmos.xta.core.engine.analysis.structural.nodes.traversers;

import java.util.Stack;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public interface NodeTraverser {
    boolean foundBlockNode(Node node, Stack<Node> ancestors);
    boolean foundFunctionNode(Node node, Stack<Node> ancestors);
    boolean foundInstructionNode(Node node, Stack<Node> ancestors);
    boolean foundSequenceNode(Node node, Stack<Node> ancestors);
    boolean foundLoopNode(Node node, Stack<Node> ancestors);
    boolean foundInfiniteLoopNode(Node node, Stack<Node> ancestors);
    boolean foundSelfLoopNode(Node node, Stack<Node> ancestors);
    boolean foundConditionalNode(Node node, Stack<Node> ancestors);
    boolean foundInfiniteSelfLoopNode(Node node, Stack<Node> ancestors);
    boolean foundEndNode(Node node, Stack<Node> ancestors);
}
