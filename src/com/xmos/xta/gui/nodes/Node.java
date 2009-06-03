package com.xmos.xta.gui.nodes;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.widgets.Shell;

import com.xmos.xta.engine.analysis.routes.Route;
import com.xmos.xta.engine.instructions.XtaInstruction;
import com.xmos.xta.gui.views.viewnode.StructureNode;
import com.xmos.xta.gui.views.viewnode.ViewNode;

public abstract class Node implements Comparable<Node> {
    private Node m_parent;
    private Vector<Node> m_nodes;
    protected StructureNode m_structureNode;
    protected ViewNode m_functionNode;
	
    public Node(Node parent) {
    	m_parent = parent;
    	m_nodes = new Vector<Node>();
    }
    
    public final int compareTo(Node node) {
    	if(node.getNumCycles() - getNumCycles() > 0){
    		return 1;
    	} else if(node.getNumCycles() - getNumCycles() == 0){
    		return 0;
    	} else {
    		return -1;
    	}
    }

    public abstract int getId();

	public abstract double getTimeTaken();
	public abstract double getTimeTakenThis();
    public abstract long getNumCycles();
    public abstract long getNumUnknowns();
        
    public abstract String getDescription();
    public abstract String getToolTipText();
    public abstract String getTypeName();
    
    public abstract boolean isBlockNode();
    public abstract boolean isLoopNode();
    public abstract boolean isPathNode();
    public abstract boolean isRouteNode();
    public abstract long getNumIterations();
    public abstract Vector<XtaInstruction> getInstructions();
    public abstract Set<Integer> getPcsCovered(boolean isSelected);
    public abstract void createContextMenu(final Shell parent, IMenuManager manager);
	public abstract void getFnopPcs(Vector<XtaInstruction> fnops);
	public abstract void getBlockIds(Set<Integer> ids);
    
    public void addNode(Node node) {
    	m_nodes.add(node);
    }

    public Vector<Node> getChildren() {
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

    public Node getPathRoot() {
    	if (isRouteNode()) 
    		return null;
    	
    	Node node = this;
    	while (!node.getParentNode().isRouteNode()) {
    		node = node.getParentNode();
    	}
		return node;
	}

	public Set<Integer> getCoverage() {
		Set<Integer> covered = new HashSet<Integer>();
		covered.addAll(getPcsCovered(true));
		for (Node node : m_nodes){
			covered.addAll(node.getCoverage());
		}
		return covered;
	}
	
    public int getRouteId() {
    	Node parent = getParentNode();
    	if (parent != null)
    		return parent.getRouteId();
    	else
    		return Route.INVALID_ROUTE_ID;
	}
}
