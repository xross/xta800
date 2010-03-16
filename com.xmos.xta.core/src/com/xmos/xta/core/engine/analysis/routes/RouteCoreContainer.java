package com.xmos.xta.core.engine.analysis.routes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.xmos.xta.core.engine.CallGraph;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.FunctionContainer;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.utils.CoreConfig;
import com.xmos.xta.core.engine.utils.NodeCorePair;

public class RouteCoreContainer {
	private Reference m_function;
	private Reference m_from;
	private Set<Reference> m_to;
	private RouteContainer m_routeContainer;
	private NodeCorePair m_nodeCore;
	private CoreConfig m_coreConfig;
    private List<Route> m_routes;
    private List<Boolean> m_exists;

    public RouteCoreContainer(Reference from, Reference to, RouteContainer routeContainer, 
    		Engine engine, int nodeNumber, int coreNumber) {

    	initialise(routeContainer, engine, nodeNumber, coreNumber);    	
    	
    	m_from = from;
		m_to = new TreeSet<Reference>();
    	m_to.add(to);    	
    }
    
    public RouteCoreContainer(Reference function, RouteContainer routeContainer, 
    		Engine engine, int nodeNumber, int coreNumber) {

    	initialise(routeContainer, engine, nodeNumber, coreNumber);    	

    	m_function = function;
    	m_from = function;
		m_to = new TreeSet<Reference>();
        for (Reference to : m_to)
        	to.resolve(engine);		
    }
    
	public Reference getFromReference() {
		return m_from;
	}

	public Set<Reference> getToReferences() {
		return m_to;
	}
	
	public Reference getFunctionReference() {
		return m_function;
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

	public NodeCorePair getNodeCore() {
		return m_nodeCore;
	}

	public int getNodeNumber() {
		return m_nodeCore.getNode();
	}

	public int getCoreNumber() {
		return m_nodeCore.getCore();
	}

	public CoreConfig getCoreConfig() {
		return m_coreConfig;
	}    
	
	private void initialise(RouteContainer routeContainer, Engine engine, int nodeNumber, int coreNumber) {
		m_routeContainer = routeContainer;
    	m_routes = new ArrayList<Route>();
    	m_exists = new ArrayList<Boolean>(); 
		m_nodeCore = new NodeCorePair(nodeNumber, coreNumber);
		m_coreConfig = new CoreConfig( 
				engine.getNumThreads(nodeNumber, coreNumber), 
				engine.getFrequency(nodeNumber));    			
	}

	public List<FunctionContainer> getFunction(String functionName, CallGraph sg){
		List<FunctionContainer> functions = new LinkedList<FunctionContainer>();
		for (FunctionContainer fn : sg.getFunctionContainers()) {
		    if (fn.getFunctionName().equals(m_routeContainer.getFunctionName())) {
		        functions.add(fn);
		    }
		}
		return functions;
    }
}
