package com.xmos.xtagui.nodes;

import com.xmos.xta.engine.Block;

public class LoopNode extends Node {
    private Block m_block;

    public LoopNode(Block block, Node parent){
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
        double timeTaken = 0.0;
        for (Block loopPath : m_block.getLoopPaths()) {
            int numIterations = m_block.getLoopPathAttributes(loopPath).getNumIterations();
            timeTaken += loopPath.getTimeTaken(true) * numIterations;
        }
        m_block.doneWithLoopPaths();
        return timeTaken;
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
    
    public String getDescription() {
        return getTimeTaken() + " ns - " + getSummary(); 
    }

    public String getToolTipText(){
        String toolTipText = getSummary() + "\n";
        toolTipText += "Total time taken: " + getTimeTaken() + " ns\n";
        toolTipText += "Total number of cycles: " + getNumCycles() + "\n";
        toolTipText += "Total number of FNOPs: " + getNumFNops() + "\n";
        toolTipText += "Total number of pausable: " + getNumPausable();
        return toolTipText;
    }
    
    public String getTypeName() {
        return "loop";
    }
        
    private int getNumIterations() {
        int numIterations = 0;
        for (Block loopPath : m_block.getLoopPaths()) {
            numIterations += m_block.getLoopPathAttributes(loopPath).getNumIterations();
        }
        m_block.doneWithLoopPaths();
        return numIterations;
    }
    
    private int getNumFNops() {
        int numFNops = 0;
        for (Block loopPath : m_block.getLoopPaths()) {
            int numIterations = m_block.getLoopPathAttributes(loopPath).getNumIterations();
            numFNops += loopPath.getNumFNops(true) * numIterations;
        }
        m_block.doneWithLoopPaths();
        return numFNops;
    }

    private int getNumPausable() {
        int numPausable = 0;
        for (Block loopPath : m_block.getLoopPaths()) {
            int numIterations = m_block.getLoopPathAttributes(loopPath).getNumIterations();
            numPausable += loopPath.getNumPausables(true) * numIterations;
        }
        m_block.doneWithLoopPaths();
        return numPausable;
    }
    
    private String getSummary() {
        String description = "Loop: " + m_block.numLoopPaths() + " path";
        if (m_block.numLoopPaths() > 1) {
            description += "s";
        }
        
        description += ", " + getNumIterations() + " iterations";
        return description;
    }        
}
