package com.xmos.xta.core.engine;

import java.util.ArrayList;
import java.util.Collection;

import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.routes.RouteContainer;
import com.xmos.xta.core.engine.analysis.routes.RouteDescription;
import com.xmos.xta.core.engine.analysis.routes.RouteTiming;
import com.xmos.xta.core.engine.analysis.routes.RouteTiming.Level;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown.State;
import com.xmos.xta.core.engine.assertions.Assertions;
import com.xmos.xta.core.engine.references.Reference;

public class Scripter {
	private Engine m_engine;
	private int m_currentRouteId;
	private Config m_currentConfig;
	private int m_currentNodeId;
	private int m_currentCoreId;
	private Collection<Reference> m_currentExclusions;
	private Assertions m_currentAssertions;
	
	public Scripter(Engine engine) {
		m_engine = engine;
		m_currentConfig = new Config();
		m_currentNodeId = 0;
		m_currentCoreId = 0;
		m_currentExclusions = new ArrayList<Reference>();
		m_currentAssertions = engine.getAssertions();
	}

	public String createScript() {
    	StringBuffer script = new StringBuffer();
 	
    	m_currentRouteId = 0;
    	script.append(String.format("load %s\n", m_engine.getExecutable().toString()));
    	    	    	
    	for (RouteContainer routeContainer : m_engine.getRouteContainers()) {
        	if (routeContainer.numExistingRoutes() <= 0)
        		continue;
    		
       		script.append("\n");

       		routeComment(routeContainer, script);
        	globalConfig(routeContainer, script);
        	addAssertions(routeContainer, script);
        	addExclusions(routeContainer, script);
        	analyseRoutes(routeContainer, script);
        	removeRoutes(routeContainer, script);

       		for (Route route : routeContainer.getRoutes()) {
       			if (routeContainer.routeExists(route)) {
           			setKnowns(route, script, routeContainer.getRoutes().size() == 1);
           			setRequirements(route, script, routeContainer.getRoutes().size() == 1);       				
       			}
       			++m_currentRouteId;
    		}
    	}
    	script.append("\n");
    	updateGlobalConfig(script, m_engine.getConfig(), m_engine.getNodeId(), m_engine.getCoreId());
    	
    	return script.toString();
    }
 	
	private void routeComment(RouteContainer routeContainer, StringBuffer script) {
    	RouteDescription routeDescription = routeContainer.getDescription();

    	script.append("#\n");
    	switch (routeDescription.getMode()) {
    	case FUNCTION:
    		script.append(String.format("# Route: %s\n", routeDescription.getFunctionName()));
    		break;
    		
    	case PATH:
    		script.append(String.format("# Route: %s => %s\n", routeDescription.getFromName(), routeDescription.getToName()));
    		break;
    	}    	    	
    	script.append("#\n");
	}
	
    private void globalConfig(RouteContainer routeContainer, StringBuffer script) {
    	updateGlobalConfig(script, 
    			routeContainer.getDescription().getConfig(), 
    			routeContainer.getDescription().getNodeId(),
    			routeContainer.getDescription().getCoreId());
    }
    
	private void addAssertions(RouteContainer routeContainer, StringBuffer script) {
		Assertions assertions = routeContainer.getDescription().getAssertions();
   		assertions.updateScript(m_currentAssertions, script, 
   				routeContainer.getDescription().getNodeId(), 
   				routeContainer.getDescription().getCoreId());
		m_currentAssertions = assertions;
	}
    
    private void addExclusions(RouteContainer routeContainer, StringBuffer script) {	
    	Collection<Reference> exclusions = routeContainer.getDescription().getExclusions();
    	
    	if (!m_currentExclusions.equals(exclusions)) {
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
		for (Unknown unknown : route.getRouteUnknowns().getUnknowns(State.KNOWN)) {	
			String routeIdStr = getRouteIdString(singleRoute);			
		    String[] refStrs = getReferenceStrings(unknown);

		    try {
		    	String scriptStr = unknown.getScriptStr(routeIdStr, refStrs);
			    outputPortableWarnings(script, unknown);
		    	script.append(scriptStr);		    	
		    } catch (Exception e) {}
		}
    }
    
    private void setRequirements(Route route, StringBuffer script, boolean singleRoute) {
		String routeIdStr = getRouteIdString(singleRoute);			    	
    	
		RouteTiming routeTiming = route.getRouteTiming();
		if (routeTiming.isTimingSpecified()) {
	    	script.append(String.format("set required %s %s\n", routeIdStr, 
	    			routeTiming.getDescription(Level.REQUIRED)));
		}
    }

    private void updateGlobalConfig(StringBuffer script, Config config, int nodeId, int coreId) {
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
    	if ((m_currentNodeId != nodeId) || (m_currentCoreId != coreId)) {
        	script.append(String.format("config proc %d %d\n", nodeId, coreId));    		
        	m_currentNodeId = nodeId;
        	m_currentCoreId = coreId;    		
    	}
    }

    private String getRouteIdString(boolean singleRoute) {
		String routeIdStr = "-";
		if (!singleRoute) {
			routeIdStr = String.format("%d", m_currentRouteId);
		}
		return routeIdStr;    	
    }

    private String[] getReferenceStrings(Unknown unknown) {
    	Collection<String> refStrings = new ArrayList<String>();
    	for (Reference reference : unknown.getReferences()) {
    		refStrings.add(getReferenceString(reference, unknown.getNode()));
    	}
    	return refStrings.toArray(new String[0]);
    }
    
    private String getReferenceString(Reference reference, Node node) {
	    String refStr = "";
		if (reference == null) {
			// This unknown was set directly onto a node (and not via a reference),
			// so we need to generate a suitable reference to use in the script.
			refStr = node.getReferenceStr();
			
		} else {
			// This unknown was set via a reference, so can use this directly in the script.
			refStr = reference.getRef();
		}
		return refStr;
    }
    
    private void outputPortableWarnings(StringBuffer script, Unknown unknown) {
    	boolean portable = true;
    	for (Reference reference : unknown.getReferences()) {
    		if ((reference == null) || (!reference.isPortable())) {
    			portable = false;
    		}
    	}    	
    	if (!portable) {
			script.append("# Warning: The following line contains a non-portable reference.\n");
		}
    }
}
