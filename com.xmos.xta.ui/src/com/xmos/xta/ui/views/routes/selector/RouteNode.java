package com.xmos.xta.ui.views.routes.selector;

import java.util.ArrayList;
import java.util.Collection;

import com.xmos.xta.core.engine.analysis.routes.Route;

public class RouteNode {
	private Route m_route;
	private RouteNode m_parent;
    private Collection<RouteNode> m_routeNodes;
    
    public RouteNode(Route route, RouteNode parent) {
    	m_route = route;
    	m_parent = parent;
    	m_routeNodes = new ArrayList<RouteNode>();
    }
    
    public Route getRoute() {
    	return m_route;
    }
    
    public RouteNode getParent() {
    	return m_parent;
    }
    
    public void addChild(Route route) {
    	m_routeNodes.add(new RouteNode(route, this));
    }
    
    public Collection<RouteNode> getChildren() {
        return m_routeNodes;   	
    }
}
