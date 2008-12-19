package com.xmos.xta.rcp.nodes;

import java.util.HashSet;
import java.util.Vector;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.widgets.Shell;
import com.xmos.xta.engine.XtaInstruction;
import com.xmos.xta.rcp.views.ViewNode.FunctionRouteNode;
import com.xmos.xta.rcp.views.ViewNode.StructureRouteNode;

public class RouteNode extends Node {
    public RouteNode() {
    	super(null);
    	m_functionNode = new FunctionRouteNode(this);
    	m_structureNode = new StructureRouteNode(this);
    }

    public int getId()         { return 0; }

    public double getTimeTaken() { return 0; }
    public double getTimeTakenThis() { return 0; }
    public int getNumCycles()    { return 0; }
    public int getNumUnknowns()  { return 0; }    
   
    public String getDescription() {
    	int numPaths = getNodes().size();
    	String str = "Found " + numPaths + " path";
    	if (numPaths != 1) {
    		str += "s";
    	}
    	return str;
    }
    public String getToolTipText() { return ""; }
    public String getTypeName()    { return "route"; }
    
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

	public void getFnopPcs(Vector<XtaInstruction> fnops) {
		// Do nothing
	}

	public void getBlockIds(Vector<Integer> ids) {
		// Do nothing
	}
}
