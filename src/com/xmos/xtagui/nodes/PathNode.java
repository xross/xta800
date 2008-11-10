package com.xmos.xtagui.nodes;

import java.util.TreeSet;
import java.util.Vector;

import com.xmos.xta.engine.Block;
import com.xmos.xta.engine.XtaInstruction;

public class PathNode implements Node {
    private Block m_block;
    private Node m_parent;
    private Vector<Node> m_nodes;
    
    public PathNode(Block block, Node parent) {
    	m_block = block;
    	m_parent = parent;
        m_nodes = new Vector<Node>();
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
//        return m_block.getNumCycles();
    	return 0;
    }

    public double getTimeTaken() {
//        return m_block.getTimeTaken();
    	return 0;
    }

    public String getToolTipText() {
        String toolTipText = "";
        toolTipText += "<HTML>" + getDescription() +  "<br>";
        toolTipText +=" <HTML>";
        return toolTipText;
    }

    public int getBranchCount() {
//        return m_block.completePaths().size();
    	return 0;
    }

    public void getAllBlocks(TreeSet<Block> blocks) {
    }  
    
    public Block getBlock() {
        return null;
    }
    
    public Vector<XtaInstruction> getInstructions() {
        return new Vector<XtaInstruction>();
    }

    public String getTypeName() {
        return "path";
    }
    
    public void addNode(Node node) {
    	m_nodes.add(node);
    }

    public Vector<Node> getNodes() {
    	return m_nodes;
    }
    
    public Node getParentNode() {
    	return m_parent;
    }    
}
