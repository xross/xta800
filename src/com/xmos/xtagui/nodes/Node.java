package com.xmos.xtagui.nodes;

import java.util.Vector;

public abstract class Node {
    private Node m_parent;
    private Vector<Node> m_nodes;	
	
    public Node(Node parent) {
    	m_parent = parent;
    	m_nodes = new Vector<Node>();
    }    
    
    public abstract int getId();
    public abstract int getInstanceId();

	public abstract double getTimeTaken();
    public abstract int getNumCycles();
        
    public abstract String getDescription();
    public abstract String getToolTipText();
    public abstract String getTypeName();    
    
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
