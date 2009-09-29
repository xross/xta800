package com.xmos.xta.core.engine.analysis.structural.nodes.traversers;

import java.util.Stack;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class DefaultNodeTraverser implements NodeTraverser {
	public boolean foundBlockNode(Node node, Stack<Node> ancestors) {
    	return true;
    }
    
    public boolean foundFunctionNode(Node node, Stack<Node> ancestors) {
    	return true;
    }
    
    public boolean foundInstructionNode(Node node, Stack<Node> ancestors) {
    	return true;
    }
    
    public boolean foundSequenceNode(Node node, Stack<Node> ancestors) {
    	return true;
    }
    
    public boolean foundLoopNode(Node node, Stack<Node> ancestors) {
    	return true;
    }
    
    public boolean foundInfiniteLoopNode(Node node, Stack<Node> ancestors) {
    	return true;
    }

    public boolean foundSelfLoopNode(Node node, Stack<Node> ancestors) {
    	return true;
    }
    
    public boolean foundConditionalNode(Node node, Stack<Node> ancestors) {
    	return true;
    }

	public boolean foundInfiniteSelfLoopNode(Node node, Stack<Node> ancestors) {
		return true;
	}
	
	public boolean foundEndNode(Node node, Stack<Node> ancestors) {
		return true;
	}
}
