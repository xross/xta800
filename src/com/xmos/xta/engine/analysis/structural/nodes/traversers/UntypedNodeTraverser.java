package com.xmos.xta.engine.analysis.structural.nodes.traversers;

import com.xmos.xta.engine.analysis.structural.nodes.Node;

public abstract class UntypedNodeTraverser implements NodeTraverser {
    public abstract boolean foundNode(Node node, int depth);
        
	public boolean foundBlockNode(Node node, int depth) {
    	return foundNode(node, depth);
    }
    
    public boolean foundFunctionNode(Node node, int depth) {
    	return foundNode(node, depth);
    }
    
    public boolean foundInstructionNode(Node node, int depth) {
    	return foundNode(node, depth);
    }
    
    public boolean foundPathNode(Node node, int depth) {
    	return foundNode(node, depth);
    }
    
    public boolean foundLoopNode(Node node, int depth) {
    	return foundNode(node, depth);
    }

    public boolean foundInfiniteLoopNode(Node node, int depth) {
    	return foundNode(node, depth);
    }
    
    public boolean foundSelfLoopNode(Node node, int depth) {
    	return foundNode(node, depth);
    }
    public boolean foundInfiniteSelfLoopNode(Node node, int depth) {
    	return foundNode(node, depth);
    }
    public boolean foundSectionNode(Node node, int depth) {
    	return foundNode(node, depth);
    }
    
    public boolean foundTerminalNode(Node node, int depth) {
    	return foundNode(node, depth);
    }
}
