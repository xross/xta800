package com.xmos.xta.engine.analysis.structural.nodes.traversers;

import com.xmos.xta.engine.analysis.structural.nodes.Node;

public class DefaultNodeTraverser implements NodeTraverser {
	public boolean foundBlockNode(Node node, int depth) {
    	return true;
    }
    
    public boolean foundFunctionNode(Node node, int depth) {
    	return true;
    }
    
    public boolean foundInstructionNode(Node node, int depth) {
    	return true;
    }
    
    public boolean foundSequenceNode(Node node, int depth) {
    	return true;
    }
    
    public boolean foundLoopNode(Node node, int depth) {
    	return true;
    }
    
    public boolean foundInfiniteLoopNode(Node node, int depth) {
    	return true;
    }

    public boolean foundSelfLoopNode(Node node, int depth) {
    	return true;
    }
    
    public boolean foundConditionalNode(Node node, int depth) {
    	return true;
    }

	public boolean foundInfiniteSelfLoopNode(Node node, int depth) {
		return true;
	}
}
