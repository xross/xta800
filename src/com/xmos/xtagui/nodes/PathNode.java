package com.xmos.xtagui.nodes;

import com.xmos.xta.engine.Block;

public class PathNode extends Node {
    private Block m_block;
    
    public PathNode(Block block, Node parent) {
    	super(parent);
    	m_block = block;
    }

    public String getDescription() {
    	return getTimeTaken() + " ns (path " + getId() + ")";
    }

    public int getId() {
        return m_block.getId();
    }

    public int getInstanceId() {
        return m_block.getInstanceId();
    }

    public int getNumCycles() {
        return m_block.getNumCycles(true);
    }

    public double getTimeTaken() {
        return m_block.getTimeTaken(true);
    }

    public String getToolTipText() {
        return getDescription();
    }

    public String getTypeName() {
        return "path";
    }    
}
