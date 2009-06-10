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
    
    public boolean foundPathNode(Node node, int depth) {
    	return true;
    }
    
    public boolean foundLoopNode(Node node, int depth) {
    	return true;
    }
    
    public boolean foundSectionNode(Node node, int depth) {
    	return true;
    }
    
    public boolean foundTerminalNode(Node node, int depth) {
    	return true;
    }
}
