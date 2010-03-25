package com.xmos.xta.core.engine.analysis.structural.nodes.traversers;

import java.util.Stack;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public interface NodeTraverserWithAncestors {
	
	TraverseStatus foundBlockNode(Node node, Stack<Node> ancestors);
	TraverseStatus foundFunctionNode(Node node, Stack<Node> ancestors);
	TraverseStatus foundInstructionNode(Node node, Stack<Node> ancestors);
	TraverseStatus foundSequenceNode(Node node, Stack<Node> ancestors);
	TraverseStatus foundLoopNode(Node node, Stack<Node> ancestors);
	TraverseStatus foundInfiniteLoopNode(Node node, Stack<Node> ancestors);
	TraverseStatus foundSelfLoopNode(Node node, Stack<Node> ancestors);
	TraverseStatus foundConditionalNode(Node node, Stack<Node> ancestors);
	TraverseStatus foundInfiniteSelfLoopNode(Node node, Stack<Node> ancestors);
	TraverseStatus foundFunctionReferenceNode(Node node, Stack<Node> ancestors);
}
