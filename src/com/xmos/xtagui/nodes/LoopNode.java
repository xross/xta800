package com.xmos.xtagui.nodes;

import java.util.TreeSet;
import java.util.Vector;

import com.xmos.xta.engine.Block;
import com.xmos.xta.engine.XtaInstruction;

public class LoopNode implements Node {
    private Block m_block;
    private Node m_parent;
    private Vector<Node> m_nodes;

    public LoopNode(Block block, Node parent){
    	m_block = block;
    	m_nodes = new Vector<Node>();
    }
        
    public String getDescription() {
        return getTimeTaken() + " ns - " + getSummary(); 
    }

    public String getToolTipText(){
        String toolTipText = "";
        toolTipText += "<HTML>" + getSummary() + "<br>";
        toolTipText += "<HTML>Total time taken: " + getTimeTaken() + " ns<br>";
        toolTipText += "<HTML>Total number of cycles: " + getNumCycles() + "<br>";
        toolTipText += "<HTML>Total number of FNOPs: " + getNumFNops() + "<br>";
        toolTipText += "<HTML>Total number of pausable: " + getNumPausable() + "<br>";
        toolTipText +=" <HTML>";
        return toolTipText;
    }
    
    public int getNumIterations() {
        int numIterations = 0;
        for (Block loopPath : m_block.getLoopPaths()) {
            numIterations += m_block.getLoopPathAttributes(loopPath).getNumIterations();
        }
        m_block.doneWithLoopPaths();
        return numIterations;
    }

    public int getId() {
        return m_block.getId();
    }

    public int getInstanceId() {
        return m_block.getInstanceId();
    }

    public int getNumCycles() {
        int numCycles = 0;
        for (Block loopPath : m_block.getLoopPaths()) {
            int numIterations = m_block.getLoopPathAttributes(loopPath).getNumIterations();
            numCycles += loopPath.getNumCycles(true) * numIterations;
        }
        m_block.doneWithLoopPaths();
        return numCycles;
    }
    
    public int getNumFNops() {
        int numFNops = 0;
        for (Block loopPath : m_block.getLoopPaths()) {
            int numIterations = m_block.getLoopPathAttributes(loopPath).getNumIterations();
            numFNops += loopPath.getNumFNops(true) * numIterations;
        }
        m_block.doneWithLoopPaths();
        return numFNops;
    }

    public int getNumPausable() {
        int numPausable = 0;
        for (Block loopPath : m_block.getLoopPaths()) {
            int numIterations = m_block.getLoopPathAttributes(loopPath).getNumIterations();
            numPausable += loopPath.getNumPausables(true) * numIterations;
        }
        m_block.doneWithLoopPaths();
        return numPausable;
    }

    public double getTimeTaken() {
        double timeTaken = 0.0;
        for (Block loopPath : m_block.getLoopPaths()) {
            int numIterations = m_block.getLoopPathAttributes(loopPath).getNumIterations();
            timeTaken += loopPath.getTimeTaken(true) * numIterations;
        }
        m_block.doneWithLoopPaths();
        return timeTaken;
    }

    public void getAllBlocks(TreeSet<Block> blocks) {
        for (Block loopPath : m_block.getLoopPaths()) {
            loopPath.getAllBlocks(blocks);
        }
        m_block.doneWithLoopPaths();
    }

    public Block getBlock() {
        return m_block;
    }

    public Vector<XtaInstruction> getInstructions() {
        Vector<XtaInstruction> insts = new Vector<XtaInstruction>();
        for (Block loopPath : m_block.getLoopPaths()) {
            insts.addAll(loopPath.getInstructions(true));
        }
        m_block.doneWithLoopPaths();
        return insts;
    }

    public String getTypeName() {
        return "loop";
    }
    
    public void addNode(Node node) {
    	m_nodes.add(node);
    }

    public Vector<Node> getNodes() {
    	return m_nodes;
    }    
    
    private String getSummary() {
        String description = "Loop: " + m_block.numLoopPaths() + " path";
        if (m_block.numLoopPaths() > 1) {
            description += "s";
        }
        
        int numIterations = getNumIterations();
        description += ", " + numIterations + " iteration";
        if (numIterations > 1) {
            description += "s";
        }
        return description;
    }    
    
    public Node getParentNode() {
    	return m_parent;
    }    
}
