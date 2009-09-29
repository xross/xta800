package com.xmos.xta.core.engine.analysis.structural.nodes.traversers;

import java.util.Stack;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public abstract class UntypedNodeTraverser implements NodeTraverser {
    public abstract boolean foundNode(Node node, Stack<Node> ancestors);
        
	public boolean foundBlockNode(Node node, Stack<Node> ancestors) {
    	return foundNode(node, ancestors);
    }
    
    public boolean foundFunctionNode(Node node, Stack<Node> ancestors) {
    	return foundNode(node, ancestors);
    }
    
    public boolean foundInstructionNode(Node node, Stack<Node> ancestors) {
    	return foundNode(node, ancestors);
    }
    
    public boolean foundSequenceNode(Node node, Stack<Node> ancestors) {
    	return foundNode(node, ancestors);
    }
    
    public boolean foundLoopNode(Node node, Stack<Node> ancestors) {
    	return foundNode(node, ancestors);
    }

    public boolean foundInfiniteLoopNode(Node node, Stack<Node> ancestors) {
    	return foundNode(node, ancestors);
    }
    
    public boolean foundSelfLoopNode(Node node, Stack<Node> ancestors) {
    	return foundNode(node, ancestors);
    }
    public boolean foundInfiniteSelfLoopNode(Node node, Stack<Node> ancestors) {
    	return foundNode(node, ancestors);
    }
    public boolean foundConditionalNode(Node node, Stack<Node> ancestors) {
    	return foundNode(node, ancestors);
    }
    
    public boolean foundEndNode(Node node, Stack<Node> ancestors) {
    	return foundNode(node, ancestors);
    }
}
