package com.xmos.xta.core.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.routes.RouteContainer;
import com.xmos.xta.core.engine.analysis.routes.RouteDescription;
import com.xmos.xta.core.engine.analysis.routes.RouteTiming;
import com.xmos.xta.core.engine.analysis.routes.RouteTiming.Level;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.Unknown;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.Unknown.State;
import com.xmos.xta.core.engine.references.Reference;

public class Scripter {
	private Engine m_engine;
	private int m_currentRouteId;
	private Config m_currentConfig;
	private int m_currentNodeId;
	private int m_currentCoreId;
	private Collection<Reference> m_currentExclusions;
	
	public Scripter(Engine engine) {
		m_engine = engine;
		m_currentConfig = new Config();
		m_currentNodeId = 0;
		m_currentCoreId = 0;
		m_currentExclusions = new ArrayList<Reference>();
	}

	public String createScript() {
    	StringBuffer script = new StringBuffer();
 	
    	m_currentRouteId = 0;
    	script.append(String.format("load %s\n", m_engine.getExecutable().toString()));
    	    	
    	for (RouteContainer routeContainer : m_engine.getRouteContainers()) {
       		script.append("\n");

       		routeComment(routeContainer, script);
        	globalConfig(routeContainer, script);
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
		for (Unknown unknown : route.getRouteUnknowns().getUnknowns(State.KNOWN)) {	
			Node node = unknown.getNode();
			
			String routeIdStr = getRouteIdString(singleRoute);			
		    String refStr = getReferenceString(unknown);

		    outputPortableWarnings(script, unknown);
		    
		    switch (unknown.getType()) {
		    case INSTRUCTION_TIME:
		    	script.append(String.format("set time %s %s %s\n", 
		    			routeIdStr, refStr, node.getAttributes().getInstructionTimeString()));
		    	break;

		    case LOOP_ITERATIONS:		    	
		    	script.append(String.format("set loop %s %s %d\n", 
		    			routeIdStr, refStr, node.getAttributes().getLoopIterations()));
		    	break;

		    case LOOP_PATH_ITERATIONS:
				Node condNode = node.getEnclosingCondNode();
		    	script.append(String.format("set looppath %s %s %d\n", 
		    			routeIdStr, refStr, condNode.getAttributes().getSetLoopPathIterations(node)));
		    	break;
		    	
		    case LOOP_SCOPE:		    	
		    	script.append(String.format("set loopscope %s %s %s\n", 
		    			routeIdStr, refStr, node.getAttributes().getIsAbsolute() ? "a" : "r"));
		    	break;
		    }
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

    private String getReferenceString(Unknown unknown) {
	    String refStr = "";
		Reference reference = unknown.getReference();
		if (reference == null) {
			// This unknown was set directly onto a node (and not via a reference),
			// so we need to generate a suitable reference to use in the script.
			List<Long> backTrail = new ArrayList<Long>();
			unknown.getNode().getBackTrail(backTrail);
			for (Long pc : backTrail) {
				refStr += String.format("CALL:0x%x,", pc);
			}				
			refStr += String.format("0x%x", unknown.getNode().getStartPc());
			
		} else {
			// This unknown was set via a reference, so can use this directly in the script.
			refStr = reference.getRef();
		}
		return refStr;
    }
    
    private void outputPortableWarnings(StringBuffer script, Unknown unknown) {
		if (unknown.getReference() == null) {
			script.append("# Warning: The following line contains a non-portable reference.\n");
		}    	
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
