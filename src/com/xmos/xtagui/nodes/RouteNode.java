package com.xmos.xtagui.nodes;

import java.util.TreeSet;
import java.util.Vector;

import com.xmos.xta.engine.Block;
import com.xmos.xta.engine.Route;
import com.xmos.xta.engine.XtaInstruction;

public class RouteNode implements Node {
    private Route m_route;
    private Vector<Node> m_nodes;
    
    public RouteNode(Route route) {
        m_route = route;
        m_nodes = new Vector<Node>();
    }

    public String getDescription() {
        int numPaths = m_route.completePaths().size();
        String description = numPaths + " complete path";
        if (numPaths > 1) {
            description += "s";
        }
        return description;
    }

    public int getId() {
        return m_route.getId();
    }

    public int getInstanceId() {
        return m_route.getInstanceId();
    }

    public int getNumCycles() {
        return m_route.getNumCycles();
    }

    public double getTimeTaken() {
        return m_route.getTimeTaken();
    }

    public String getToolTipText() {
        String toolTipText = "";
        toolTipText += "<HTML>" + getDescription() +  "<br>";
        toolTipText +=" <HTML>";
        return toolTipText;
    }

    public void getAllBlocks(TreeSet<Block> blocks) {
    }  
    
    public Block getBlock() {
        return null;
    }
    
    public Route getRoute(){
        return m_route;
    }

    public Vector<XtaInstruction> getInstructions() {
        return new Vector<XtaInstruction>();
    }

    public String getTypeName() {
        return "route";
    }
    
    public void addNode(Node node) {
    	m_nodes.add(node);
    }

    public Vector<Node> getNodes() {
    	return m_nodes;
    }    
    
    public Node getParentNode() {
    	return null;
    }    
}
