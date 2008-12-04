package com.xmos.xta.rcp.nodes;

import java.util.HashSet;
import java.util.Vector;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.widgets.Shell;

import com.xmos.xta.engine.XtaInstruction;
import com.xmos.xta.rcp.views.ViewNode.StructureNode;
import com.xmos.xta.rcp.views.ViewNode.ViewNode;

public abstract class Node {
    private Node m_parent;
    private Vector<Node> m_nodes;
    protected StructureNode m_structureNode;
    protected ViewNode m_functionNode;
	
    public Node(Node parent) {
    	m_parent = parent;
    	m_nodes = new Vector<Node>();
    }
    
    public abstract int getId();

	public abstract double getTimeTaken();
	public abstract double getTimeTakenThis();
    public abstract int getNumCycles();
    public abstract int getNumUnknowns();
        
    public abstract String getDescription();
    public abstract String getToolTipText();
    public abstract String getTypeName();
    
    public abstract boolean isBlockNode();
    public abstract boolean isLoopNode();
    public abstract boolean isPathNode();
    public abstract boolean isRouteNode();
    public abstract int getNumIterations();
    public abstract Vector<XtaInstruction> getInstructions();
    public abstract HashSet<Integer> getPcsCovered();
    public abstract void createContextMenu(final Shell parent, IMenuManager manager);
	public abstract void getBlockIds(Vector<Integer> ids);
    
    public void addNode(Node node) {
    	m_nodes.add(node);
    }

    public Vector<Node> getNodes() {
    	return m_nodes;
    }    
    
    public Node getParentNode() {
    	return m_parent;
    }
    
    public StructureNode getStructureNode() {
    	return m_structureNode;
    }

    public ViewNode getFunctionNode() {
    	return m_functionNode;
    }
}
