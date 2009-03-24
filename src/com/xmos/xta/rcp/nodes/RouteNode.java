package com.xmos.xta.rcp.nodes;

import java.util.HashSet;
import java.util.Vector;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Shell;

import com.xmos.xta.engine.Route;
import com.xmos.xta.engine.RouteTiming;
import com.xmos.xta.engine.XtaInstruction;
import com.xmos.xta.rcp.XtaEngine;
import com.xmos.xta.rcp.dialogs.Required;
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
    public long getNumCycles() { return 0; }
    public long getNumUnknowns() { return 0; }    
   
    public String getDescription() {
    	String str = "Route (" + m_route.getRouteId() + ") " + m_route.getDescription() + "";
    	return str;
    }
    public String getToolTipText() {
        return m_route.getFullDescription();
    }
    
    public String getTypeName() {
    	RouteTiming routeTiming = m_route.getRouteTiming();
		if (routeTiming.isTimingSpecified()) {
			if (routeTiming.passedTiming())
				return "required_pass";
			else
				return "required_fail";
		} else {
			return "required_notset";
		}
	}
    	
    
    public HashSet<Integer> getPcsCovered() {
    	return new HashSet<Integer>();
    }

    public void createContextMenu(final Shell parent, IMenuManager manager) {
	    manager.add(new Separator());
	    manager.add(new Action("Set timing requirements") {
		    public void run() {
			    new Required(parent, XtaEngine.getEngine(), m_route);
		    }
	    });
	    manager.add(new Action("Clear timing requirements") {
		    public void run() {
			    XtaEngine.getEngine().unsetRouteMax(m_route.getRouteId());
		    }
	    });
    }
    
	public long getNumIterations() { return 1; }
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
