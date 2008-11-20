package com.xmos.xtagui.nodes;

import java.util.HashSet;

import org.eclipse.jface.action.IMenuManager;

import com.xmos.xta.engine.Route;
import com.xmos.xtagui.views.TreeView;

public class RouteNode extends Node {
    private Route m_route;
    
    public RouteNode(Route route) {
    	super(null);
        m_route = route;
    }

    public int getId() {
    	assert(false);
        return 0;
    }

    public int getInstanceId() {
    	assert(false);
        return 0;
    }

    public double getTimeTaken() {
    	assert(false);
        return 0;
    }

    public int getNumCycles() {
    	assert(false);
        return 0;
    }
    
    public String getDescription() {
        int numPaths = m_route.completePaths().size();
        String description = numPaths + " complete path";
        if (numPaths > 1) {
            description += "s";
        }
        return description;
    }

    public String getToolTipText() {
        return getDescription();
    }

    public String getTypeName() {
        return "route";
    }
    
    public HashSet<Integer> getPcsCovered() {
    	assert(false);
        return null;	
    }    
    
    public void createContextMenu(IMenuManager manager, final TreeView treeView) {    	
    	assert(false);
    }    
}
