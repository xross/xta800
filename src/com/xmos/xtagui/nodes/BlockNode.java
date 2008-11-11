package com.xmos.xtagui.nodes;

import com.xmos.xta.engine.Block;

public class BlockNode extends Node {
	Block m_block;
	
    public BlockNode(Block block, Node parent) {
    	super(parent);
    	m_block = block; 
    }
    
    public int getId() {
        return m_block.getId();
    }
    
    public int getInstanceId() {
        return m_block.getInstanceId();
    }

    public double getTimeTaken() {
        return m_block.getTimeTakenThis();
    }
    
    public int getNumCycles() {
        return m_block.getNumCyclesThis();
    }
        
    public String getDescription() {
        return getTimeTaken() + " ns";
    }
    
    public String getToolTipText() {
        String toolTipText = "";
        toolTipText += "Block Instance ID: " + m_block.getInstanceId() + "\n";
        toolTipText += "Time taken: " + getTimeTaken() + " ns\n";
        toolTipText += "Number of cycles: " + getNumCycles() + "\n";
        toolTipText += "Number of FNOPs: " + m_block.getNumFNopsThis() + "\n";
        toolTipText += "Number of pausable: " + m_block.getNumPausablesThis();
        return toolTipText;
    }

    public String getTypeName() {
        return "block";
    }
}
