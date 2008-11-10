package com.xmos.xtagui.nodes;

import java.util.TreeSet;
import java.util.Vector;
import com.xmos.xta.engine.Block;
import com.xmos.xta.engine.XtaInstruction;

public class BlockNode implements Node {
    private Block m_block;
    private Node m_parent;
    private Vector<Node> m_nodes;
    

    public BlockNode(Block block, Node parent) {
    	m_block = block; 
    	m_parent = parent;
    	m_nodes = new Vector<Node>();
    }
    
    public int getId() {
        return m_block.getId();
    }
    
    public int getInstanceId() {
        return m_block.getInstanceId();
    }
    
    public int getNumCycles() {
        return m_block.getNumCyclesThis();
    }
    
    public double getTimeTaken() {
        return m_block.getTimeTakenThis();
    }
    
    public String getDescription() {
        return getTimeTaken() + " ns";
    }
    
    public String getToolTipText() {
        String toolTipText = "";
        toolTipText += "<HTML>Block Instance ID: " + m_block.getInstanceId() + "<br>";
        toolTipText += "<HTML>Time taken: " + getTimeTaken() + " ns<br>";
        toolTipText += "<HTML>Number of cycles: " + getNumCycles() + "<br>";
        toolTipText += "<HTML>Number of FNOPs: " + m_block.getNumFNopsThis() + "<br>";
        toolTipText += "<HTML>Number of pausable: " + m_block.getNumPausablesThis() + "<br>";
        toolTipText +=" <HTML>";
        return toolTipText;
    }

    public void getAllBlocks(TreeSet<Block> blocks) {
        blocks.add(m_block);
    }
    
    public Block getBlock() {
        return m_block;
    }

    public Vector <XtaInstruction> getInstructions() {
        return m_block.getInstructionsThis();
    }

    public String getTypeName() {
        return "block";
    }
    
    public void addNode(Node node) {
    	assert(false);
    }

    public Vector<Node> getNodes() {
    	return m_nodes;
    }    
    
    public Node getParentNode() {
    	return m_parent;
    }
}
