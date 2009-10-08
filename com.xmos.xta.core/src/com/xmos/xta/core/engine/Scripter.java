package com.xmos.xta.core.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.routes.RouteContainer;
import com.xmos.xta.core.engine.analysis.routes.RouteDescription;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes.UnknownState;
import com.xmos.xta.core.engine.references.Reference;

public class Scripter {
	private static final boolean DEBUG = true;
	
	private Engine m_engine;
	private int m_currentRouteId;
	private Config m_currentConfig;
	private Collection<Reference> m_currentExclusions;
	
	public Scripter(Engine engine) {
		m_engine = engine;
		m_currentConfig = new Config();
		m_currentExclusions = new ArrayList<Reference>();
	}

	public String createScript() {
    	StringBuffer script = new StringBuffer();
    	
    	m_currentRouteId = 0;
    	script.append(String.format("load %s\n", m_engine.getExecutable().toString()));
    	    	
    	for (RouteContainer routeContainer : m_engine.getRouteContainers()) {
       		script.append("\n");

        	globalConfig(routeContainer, script);
        	addExclusions(routeContainer, script);
        	analyseRoutes(routeContainer, script);
        	removeRoutes(routeContainer, script);

       		for (Route route : routeContainer.getRoutes()) {
       			if (routeContainer.routeExists(route)) {
           			setKnowns(route, script, routeContainer.getRoutes().size() == 1);
           			setRequirements(route, script);       				
       			}
       			++m_currentRouteId;
    		}
    	}
    	return script.toString();
    }
    
    private void globalConfig(RouteContainer routeContainer, StringBuffer script) {
    	Config config = routeContainer.getDescription().getConfig();
    	
    	if (m_currentConfig.m_numThreads != config.m_numThreads) {
        	script.append(String.format("config threads %s\n", config.m_numThreads));
        	m_currentConfig.m_numThreads = config.m_numThreads;    		
    	}
    	if (m_currentConfig.m_frequency != config.m_frequency) {
        	script.append(String.format("config freq %s\n", config.m_frequency));    		
        	m_currentConfig.m_frequency = config.m_frequency; 
    	}
    	if (m_currentConfig.m_case != config.m_case) {
        	script.append(String.format("config case %s\n", config.m_case));    		
        	m_currentConfig.m_case = config.m_case; 
    	}
    }
    
    private void addExclusions(RouteContainer routeContainer, StringBuffer script) {	
    	Collection<Reference> exclusions = routeContainer.getDescription().getExclusions();
    	
    	if (exclusionsDiffer(m_currentExclusions, exclusions)) {
    		if (!m_currentExclusions.isEmpty()) {
        	    script.append("remove exclusion *\n");
    		}
        	for (Reference exclusion : exclusions) {
            	script.append(String.format("add exclusion %s\n", exclusion.getRef()));    		
        	}    		    		
    		m_currentExclusions.clear();
    		m_currentExclusions.addAll(exclusions);
    	}    	
    }

    private void analyseRoutes(RouteContainer routeContainer, StringBuffer script) {
    	RouteDescription routeDescription = routeContainer.getDescription();
    	switch (routeDescription.getMode()) {
    	case FUNCTION:
    		script.append(String.format("analyse function %s\n", routeDescription.getFunctionName()));
    		break;
    		
    	case PATH:
    		script.append(String.format("analyse path %s %s\n", routeDescription.getFromName(), routeDescription.getToName()));
    		break;
    	}    	
    }
    
    private void removeRoutes(RouteContainer routeContainer, StringBuffer script) {
    	int index = 0;
   		for (Route route : routeContainer.getRoutes()) {
   			if (!routeContainer.routeExists(route)) {
   				script.append(String.format("remove route %d\n", m_currentRouteId + index));
   			}
   			++index;
   		}
    }

    private void setKnowns(Route route, StringBuffer script, boolean singleRoute) {
		for (Node node : route.getRouteUnknowns().getUnknowns(UnknownState.KNOWN)) {
        	if (DEBUG) System.out.printf("    - %s, pc: 0x%x, backtrace: %s\n", 
		    	    node.getAttributes().getUnknownDescription(),
		    		node.getStartPc(), node.getBackTrace(""));

		    System.out.printf("      - Reference: %s\n", node.getAttributes().getReference().toString());
		    
			assert (node.getAttributes().getReference() != null);
		    switch (node.getAttributes().getUnknownType()) {
		    case INSTRUCTION_TIME:
		    	// TODO script += String.format("set time - %s \n", routeDescription.getFunctionName());
		    	break;

		    case LOOP_ITERATIONS:
		    	// TODO script += String.format("set loop - %s \n", routeDescription.getFunctionName());
		    	break;

		    case LOOP_PATH_ITERATIONS:
		    	// TODO script += String.format("set looppath - %s \n", routeDescription.getFunctionName());
		    	break;
		    }
	    }    	
    }
    
    private void setRequirements(Route route, StringBuffer script) {
    	// TODO
//		RouteTiming routeTiming = oldRoute.getRouteTiming();
//		if (routeTiming.isTimingSpecified()) {
//			System.out.printf("    - Timing: %f\n", routeTiming.getMaxTime());
//		}			
    	
    }
    
    private boolean exclusionsDiffer(Collection<Reference> ex1, Collection<Reference> ex2) {
    	if (ex1.size() != ex2.size())
    		return true;
    	
    	Iterator<Reference> iter1 = ex1.iterator();
    	Iterator<Reference> iter2 = ex2.iterator();

    	while (iter1.hasNext() && iter2.hasNext()) {
    		if (iter1.next() != iter2.next()) {
    			return true;
    		}
    	}    	
        return false;
    }
}
