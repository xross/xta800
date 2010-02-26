package com.xmos.xta.core.engine.analysis.structural.nodes.traversers;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public abstract class UntypedNodeTraverser implements NodeTraverser {
    public abstract TraverseStatus foundNode(Node node);

	public TraverseStatus foundBlockNode(Node node) {
    	return foundNode(node);
    }

    public TraverseStatus foundFunctionNode(Node node) {
    	return foundNode(node);
    }

    public TraverseStatus foundFunctionReferenceNode(Node node) {
    	return foundNode(node);
    }

    public TraverseStatus foundInstructionNode(Node node) {
    	return foundNode(node);
    }

    public TraverseStatus foundSequenceNode(Node node) {
    	return foundNode(node);
    }

    public TraverseStatus foundLoopNode(Node node) {
    	return foundNode(node);
    }

    public TraverseStatus foundInfiniteLoopNode(Node node) {
    	return foundNode(node);
    }

    public TraverseStatus foundSelfLoopNode(Node node) {
    	return foundNode(node);
    }

    public TraverseStatus foundInfiniteSelfLoopNode(Node node) {
    	return foundNode(node);
    }
    public TraverseStatus foundConditionalNode(Node node) {
    	return foundNode(node);
    }

    public TraverseStatus foundEndNode(Node node) {
    	return foundNode(node);
    }
}
