package com.xmos.xta.rcp.nodes;

import java.util.HashSet;
import java.util.Vector;

import org.eclipse.jface.action.IMenuManager;

import com.xmos.xta.engine.XtaInstruction;
import com.xmos.xta.rcp.views.TreeView;

public class RouteNode extends Node {
    public RouteNode() { super(null); }

    public int getId()         { assert(false); return 0; }
    public int getInstanceId() { assert(false); return 0; }

    public double getTimeTaken() { assert(false); return 0; }
    public int getNumCycles()    { assert(false); return 0; }
    
    public String getDescription() { assert(false); return ""; }
    public String getToolTipText() { assert(false); return ""; }
    public String getTypeName()    { return "route"; }
    
    public HashSet<Integer> getPcsCovered() { assert(false); return null; }       
    public void createContextMenu(IMenuManager manager, final TreeView treeView) { assert(false); }
    
	public int getNumIterations() { assert(false); return 0; }
	public boolean isLoopNode() { assert(false); return false; }
	public boolean isPathNode() { assert(false); return false; }

    public Vector<XtaInstruction> getInstructions() { assert(false); return null; };
}
