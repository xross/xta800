package com.xmos.xta.rcp.nodes;

import java.util.HashSet;
import java.util.Vector;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.widgets.Shell;

import com.xmos.xta.engine.XtaInstruction;

public class HiddenNode extends Node {
    public HiddenNode() {
    	super(null);
    }

    public int getId()         { return 0; }
    public double getTimeTaken() { return 0; }
    public double getTimeTakenThis() { return 0; }
    public long getNumCycles()    { return 0; }
    public long getNumUnknowns()  { return 0; }    
   
    public String getDescription() { return ""; }
    public String getToolTipText() { return ""; }
    public String getTypeName()    { return ""; }
    
    public HashSet<Integer> getPcsCovered(boolean isSelected) { return new HashSet<Integer>(); }
    public void createContextMenu(final Shell parent, IMenuManager manager) {}
    
	public long getNumIterations() { return 1; }
	public boolean isBlockNode() { return false; }
	public boolean isLoopNode() { return false; }
	public boolean isPathNode() { return false; }
	public boolean isRouteNode() { return true; }

    public Vector<XtaInstruction> getInstructions() { return new Vector<XtaInstruction>(); }

    public void getFnopPcs(Vector<XtaInstruction> fnops) {}

	public void getBlockIds(HashSet<Integer> ids) {}
}
