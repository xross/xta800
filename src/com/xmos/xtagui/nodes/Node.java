package com.xmos.xtagui.nodes;

import java.util.TreeSet;
import java.util.Vector;

import com.xmos.xta.engine.Block;
import com.xmos.xta.engine.XtaInstruction;

public interface Node {
    public double getTimeTaken();
    public int getNumCycles();
    public int getId();
    public int getInstanceId();
    
    public Vector<XtaInstruction> getInstructions();
    
    public String getDescription();
    public String getToolTipText();
    public String getTypeName();    
    
    public Block getBlock();    
    public void getAllBlocks(TreeSet<Block> blocks);

    public void addNode(Node node);
    public Vector<Node> getNodes();
    public Node getParentNode();
}
