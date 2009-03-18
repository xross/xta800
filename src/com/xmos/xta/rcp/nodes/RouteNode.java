package com.xmos.xta.rcp.nodes;

import java.util.HashSet;
import java.util.Vector;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.widgets.Shell;

import com.xmos.xta.engine.Route;
import com.xmos.xta.engine.XtaInstruction;
import com.xmos.xta.rcp.views.ViewNode.FunctionRouteNode;
import com.xmos.xta.rcp.views.ViewNode.StructureRouteNode;

public class RouteNode extends Node {
	private Route m_route;
	
    public RouteNode(Route route) {
    	super(null);
    	m_route = route;

    	m_functionNode = new FunctionRouteNode(this);
    	m_structureNode = new StructureRouteNode(this);
    }

    public int getId() { return m_route.getRouteId(); }

    public double getTimeTaken() { return 0; }
    public double getTimeTakenThis() { return 0; }
    public int getNumCycles() { return 0; }
    public int getNumUnknowns() { return 0; }    
   
    public String getDescription() {
    	String str = "Route " + m_route.getRouteId() + " (" + m_route.getDescription() + ")";
    	return str;
    }
    public String getToolTipText() {
        String toolTipText = "";
        toolTipText += "Route ID: " + m_route.getRouteId() + "\n";
        toolTipText += "----------------------------------\n";
        toolTipText += "Description: " + m_route.getDescription() + "\n";
		toolTipText += "Completed paths: " + m_route.getCompletedPaths().size() + "\n";
		toolTipText += "Unresolved paths: " + m_route.getUnresolvedPaths().size() + "\n";
        
        return toolTipText;
    }
    
    public String getTypeName() { return "route"; }
    
    public HashSet<Integer> getPcsCovered() {
    	return new HashSet<Integer>();
    }
    public void createContextMenu(final Shell parent, IMenuManager manager) {
    	// Do nothing
    }
    
	public int getNumIterations() { return 1; }
	public boolean isBlockNode() { return false; }
	public boolean isLoopNode() { return false; }
	public boolean isPathNode() { return false; }
	public boolean isRouteNode() { return true; }

    public Vector<XtaInstruction> getInstructions() {
    	return new Vector<XtaInstruction>();
    }

    public Vector<XtaInstruction> getUnknowns() {
		return new Vector<XtaInstruction>();
	}

	public void getFnopPcs(Vector<XtaInstruction> fnops) {
		// Do nothing
	}

	public void getBlockIds(Vector<Integer> ids) {
		// Do nothing
	}
}
