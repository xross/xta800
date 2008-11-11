package com.xmos.xtagui.nodes;

import com.xmos.xta.engine.Route;

public class RouteNode extends Node {
    private Route m_route;
    
    public RouteNode(Route route) {
    	super(null);
        m_route = route;
    }

    public int getId() {
        return m_route.getId();
    }

    public int getInstanceId() {
        return m_route.getInstanceId();
    }

    public double getTimeTaken() {
        return m_route.getTimeTaken();
    }

    public int getNumCycles() {
        return m_route.getNumCycles();
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
}
