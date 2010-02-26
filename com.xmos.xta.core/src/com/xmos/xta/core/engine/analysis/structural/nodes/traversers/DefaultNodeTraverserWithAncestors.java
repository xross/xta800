package com.xmos.xta.core.engine.analysis.structural.nodes.traversers;

import java.util.Stack;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class DefaultNodeTraverserWithAncestors implements NodeTraverserWithAncestors {
	public TraverseStatus foundBlockNode(Node node, Stack<Node> ancestors) {
    	return TraverseStatus.CONTINUE;
    }

    public TraverseStatus foundFunctionNode(Node node, Stack<Node> ancestors) {
    	return TraverseStatus.CONTINUE;
    }

    public TraverseStatus foundFunctionReferenceNode(Node node, Stack<Node> ancestors) {
    	return TraverseStatus.CONTINUE;
    }

    public TraverseStatus foundInstructionNode(Node node, Stack<Node> ancestors) {
    	return TraverseStatus.CONTINUE;
    }

    public TraverseStatus foundSequenceNode(Node node, Stack<Node> ancestors) {
    	return TraverseStatus.CONTINUE;
    }

    public TraverseStatus foundLoopNode(Node node, Stack<Node> ancestors) {
    	return TraverseStatus.CONTINUE;
    }

    public TraverseStatus foundInfiniteLoopNode(Node node, Stack<Node> ancestors) {
    	return TraverseStatus.CONTINUE;
    }

    public TraverseStatus foundSelfLoopNode(Node node, Stack<Node> ancestors) {
    	return TraverseStatus.CONTINUE;
    }

    public TraverseStatus foundConditionalNode(Node node, Stack<Node> ancestors) {
    	return TraverseStatus.CONTINUE;
    }

	public TraverseStatus foundInfiniteSelfLoopNode(Node node, Stack<Node> ancestors) {
		return TraverseStatus.CONTINUE;
	}

	public TraverseStatus foundEndNode(Node node, Stack<Node> ancestors) {
		return TraverseStatus.CONTINUE;
	}
}
