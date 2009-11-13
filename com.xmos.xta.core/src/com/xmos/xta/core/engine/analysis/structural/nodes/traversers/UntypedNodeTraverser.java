package com.xmos.xta.core.engine.analysis.structural.nodes.traversers;

import java.util.Stack;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public abstract class UntypedNodeTraverser implements NodeTraverser {
    public abstract TraverseStatus foundNode(Node node, Stack<Node> ancestors);
        
	public TraverseStatus foundBlockNode(Node node, Stack<Node> ancestors) {
    	return foundNode(node, ancestors);
    }
    
    public TraverseStatus foundFunctionNode(Node node, Stack<Node> ancestors) {
    	return foundNode(node, ancestors);
    }
    
    public TraverseStatus foundInstructionNode(Node node, Stack<Node> ancestors) {
    	return foundNode(node, ancestors);
    }
    
    public TraverseStatus foundSequenceNode(Node node, Stack<Node> ancestors) {
    	return foundNode(node, ancestors);
    }
    
    public TraverseStatus foundLoopNode(Node node, Stack<Node> ancestors) {
    	return foundNode(node, ancestors);
    }

    public TraverseStatus foundInfiniteLoopNode(Node node, Stack<Node> ancestors) {
    	return foundNode(node, ancestors);
    }
    
    public TraverseStatus foundSelfLoopNode(Node node, Stack<Node> ancestors) {
    	return foundNode(node, ancestors);
    }

    public TraverseStatus foundInfiniteSelfLoopNode(Node node, Stack<Node> ancestors) {
    	return foundNode(node, ancestors);
    }
    public TraverseStatus foundConditionalNode(Node node, Stack<Node> ancestors) {
    	return foundNode(node, ancestors);
    }
    
    public TraverseStatus foundEndNode(Node node, Stack<Node> ancestors) {
    	return foundNode(node, ancestors);
    }
}
