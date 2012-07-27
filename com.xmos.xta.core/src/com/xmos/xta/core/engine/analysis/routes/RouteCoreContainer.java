package com.xmos.xta.core.engine.analysis.routes;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.xmos.libs.core.utils.CoreId;
import com.xmos.libs.core.utils.IteratableSet;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.utils.CoreConfig;

public class RouteCoreContainer {
	private Reference m_reference;
	private Reference m_from;
	private Set<Reference> m_to;
	private RouteContainer m_routeContainer;
	private CoreId m_coreId;
	private CoreConfig m_coreConfig;
    private List<Route> m_routes;
    private List<Boolean> m_exists;

    public RouteCoreContainer(Reference from, Reference to, RouteContainer routeContainer, 
    		Engine engine, CoreId coreId) {

    	initialise(routeContainer, engine, coreId);    	
    	
    	m_from = from;
		m_to = new IteratableSet<Reference>();
    	m_to.add(to);    	
    }
    
    public RouteCoreContainer(Reference reference, RouteContainer routeContainer,  Engine engine, CoreId coreId) {
    	initialise(routeContainer, engine, coreId);    	

    	m_reference = reference;
    	m_from = reference;
		m_to = new IteratableSet<Reference>();
    }
    
	public Reference getFromReference() {
		return m_from;
	}

	public Set<Reference> getToReferences() {
		return m_to;
	}
	
	public Reference getReference() {
		return m_reference;
	}
	
	public RouteContainer getRouteContainer() {
		return m_routeContainer;
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

    public int getNumExistingRoutes() {
    	int num = 0;
        for (Route route : m_routes) {
        	if (routeExists(route)) {
        		++num;
        	}
        }
        return num;
    }

	public CoreId getCoreId() {
		return m_coreId;
	}

	public CoreConfig getCoreConfig() {
		return m_coreConfig;
	}    
	
	private void initialise(RouteContainer routeContainer, Engine engine, CoreId coreId) {
		m_routeContainer = routeContainer;
    	m_routes = new ArrayList<Route>();
    	m_exists = new ArrayList<Boolean>(); 
    	m_coreId = coreId;
		m_coreConfig = new CoreConfig(engine.getNumThreads(m_coreId), engine.getFrequency(coreId.getNodeId()));    			
	}
}
