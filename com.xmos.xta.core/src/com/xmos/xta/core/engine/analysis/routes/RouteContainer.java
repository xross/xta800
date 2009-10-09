package com.xmos.xta.core.engine.analysis.routes;

import java.util.ArrayList;
import java.util.List;

public class RouteContainer {
    private RouteDescription m_description;
    private List<Route> m_routes;
    private List<Boolean> m_exists;
    
    public RouteContainer(RouteDescription description) {
    	m_description = description;
    	m_routes = new ArrayList<Route>();
    	m_exists = new ArrayList<Boolean>();
    }
    
    public RouteDescription getDescription() {
    	return m_description;
    }
    
    public void add(List<Route> routes) {
    	m_routes.addAll(routes);
    	for (int i = 0; i < m_routes.size(); ++i) {
    		m_exists.add(true);
    	}
    }

    public void remove(Route route) {
    	int index = m_routes.indexOf(route);
    	m_exists.set(index, false);
    }

    public List<Route> getRoutes() {
    	return m_routes;    	
    }
    
    public boolean routeExists(Route route) {
    	int index = m_routes.indexOf(route);
    	return m_exists.get(index);
    }
}
